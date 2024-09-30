package com.adyen.android.assignment.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class RequestPermissionsLauncher(
    private val context: Context, // always app context
    caller: ActivityResultCaller,
    private val requestedPermissions: Array<String>,
) {
    private val launcher: ActivityResultLauncher<Array<String>>
    private var permissionRequestResultHandler: ((PermissionRequestResult) -> Unit)? = null

    init {
        launcher =
            caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                val granted = requestedPermissions.filter { result.getOrDefault(it, false) }

                when {
                    granted.isEmpty() -> permissionRequestResultHandler?.invoke(
                        PermissionRequestResult.Denied
                    )

                    granted.size == requestedPermissions.size -> permissionRequestResultHandler?.invoke(
                        PermissionRequestResult.Granted
                    )

                    else -> {
                        permissionRequestResultHandler?.invoke(
                            PermissionRequestResult.GrantedPartially(
                                granted
                            )
                        )
                    }
                }
            }
    }

    operator fun invoke(permissionRequestResultHandler: (PermissionRequestResult) -> Unit) {
        //register handler
        this.permissionRequestResultHandler = permissionRequestResultHandler

        if (requestedPermissions.isEmpty()) {
            permissionRequestResultHandler.invoke(PermissionRequestResult.Granted)
            return
        }

        val allPermissionsGranted = requestedPermissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            permissionRequestResultHandler.invoke(PermissionRequestResult.Granted)
            return
        }

        launcher.launch(requestedPermissions)
    }

    sealed interface PermissionRequestResult {
        data object Granted : PermissionRequestResult
        data class GrantedPartially(val granted: List<String>) : PermissionRequestResult
        data object Denied : PermissionRequestResult
    }
}