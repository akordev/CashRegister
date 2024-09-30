package com.adyen.android.assignment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adyen.android.assignment.domain.models.Venue
import com.adyen.android.assignment.domain.usecase.RequestPermissionsLauncher
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainScreen(
    permissionsUseCase: RequestPermissionsLauncher,
    viewModel: MainViewModel = koinViewModel()
) {

    val viewState = viewModel.viewState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        permissionsUseCase.invoke { permissionRequestResult ->
            when (permissionRequestResult) {
                RequestPermissionsLauncher.PermissionRequestResult.Denied,
                is RequestPermissionsLauncher.PermissionRequestResult.GrantedPartially -> {
                    viewModel.handleEvents(MainViewModel.Event.PermissionDenied) // assume we require all requested permissions
                }

                RequestPermissionsLauncher.PermissionRequestResult.Granted -> viewModel.handleEvents(
                    MainViewModel.Event.PermissionGranted
                )
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.handleEvents(MainViewModel.Event.GoToBackground)
    }

    when (val state = viewState.value) {
        is MainViewModel.ViewState.Loading -> LoadingState()
        is MainViewModel.ViewState.VenueList -> VenueListState(state.data)
        is MainViewModel.ViewState.PermissionDenied -> PermissionDeniedState()
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueListState(
    venues: List<Venue>
) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text("Venues") })
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(items = venues) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp)
                        .padding(4.dp),
                ) {
                    Text(text = item.name)
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedState() {
    Box {
        Text(
            textAlign = TextAlign.Center,
            text = "Permissions required"
        )
    }
}