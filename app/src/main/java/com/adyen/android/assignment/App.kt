package com.adyen.android.assignment

import android.app.Application
import androidx.activity.result.ActivityResultCaller
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.domain.usecase.GetUserLocationUpdatesUseCase
import com.adyen.android.assignment.domain.usecase.GetVenuesUseCase
import com.adyen.android.assignment.domain.usecase.RequestPermissionsLauncher
import com.adyen.android.assignment.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}

private val appModule = module {

    single<DispatcherProvider> {
        DefaultDispatcherProvider()
    }

    factory<RequestPermissionsLauncher> { (caller: ActivityResultCaller, requestedPermissions: Array<String>) ->
        RequestPermissionsLauncher(
            context = get(),
            caller = caller,
            requestedPermissions = requestedPermissions
        )
    }

    factory<GetUserLocationUpdatesUseCase> {
        GetUserLocationUpdatesUseCase(
            // we use app context, so location client will be attach to the process
            // and not our activity, means we can get updates when app is in background (we might not want it, so should be discussed)
            context = androidContext(),
            dispatcherProvider = get<DispatcherProvider>()
        )
    }

    factory { PlacesService.instance }

    factory {
        GetVenuesUseCase(
            get(), get()
        )
    }

    viewModel {
        MainViewModel(
            get(), get()
        )
    }
}