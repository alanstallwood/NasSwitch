package com.alanstallwood.nasswitch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alanstallwood.nasswitch.ui.model.NasViewModel

@Composable
fun NasScreen(
    viewModel: NasViewModel,
    onConfigClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = when (state.isOnline) {
                true -> "ONLINE"
                false -> "OFFLINE"
                null -> "UNKNOWN"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = when (state.isOnline) {
                true -> Color.Green
                false -> Color.Red
                null -> Color.Gray
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = { viewModel.wake() },
                enabled = state.isLoading.not()
            ) {
                Text("ON")
            }

            Button(
                onClick = { viewModel.shutdown() },
                enabled = state.isLoading.not()
            ) {
                Text("OFF")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(onClick = { viewModel.refreshStatus() }) {
            Text("Refresh")
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onConfigClick) {
            Text("Settings")
        }
    }
}