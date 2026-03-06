package com.alanstallwood.nasswitch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alanstallwood.nasswitch.ui.model.NasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasScreen(
    viewModel: NasViewModel,
    onConfigClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NAS Switch") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onConfigClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                style = MaterialTheme.typography.headlineLarge,
                color = when (state.isOnline) {
                    true -> Color(0xFF4CAF50) // Material Green
                    false -> Color(0xFFF44336) // Material Red
                    null -> Color.Gray
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                // Placeholder to keep layout stable
                Spacer(modifier = Modifier.height(40.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.wake() },
                    enabled = state.isOnline == false && !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Wake")
                }

                Button(
                    onClick = { viewModel.shutdown() },
                    enabled = state.isOnline == true && !state.isLoading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Shutdown")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { viewModel.refreshStatus() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Status")
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
