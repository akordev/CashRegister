package com.adyen.android.assignment.ui

import androidx.activity.ComponentActivity

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.adyen.android.assignment.domain.usecase.RequestPermissionsLauncher
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity(), KoinComponent {

    private val permissionsUseCase: RequestPermissionsLauncher = get {
        parametersOf(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(
                    permissionsUseCase
                )
            }
        }
    }

}