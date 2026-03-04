package com.alanstallwood.nasswitch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import kotlinx.coroutines.launch

@Composable
fun NasConfigScreen(
    dataStore: NasPreferencesDataStore,
    onBack: () -> Unit
) {
    val configFlow = dataStore.nasConfigFlow.collectAsState(initial = emptyMap())
    val config = configFlow.value
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(
            value = config["macAddress"] ?: "",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("macAddress", newValue)
                }
            },
            label = { Text("MAC Address") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["broadcastIp"] ?: "",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("broadcastIp", newValue)
                }
            },
            label = { Text("Broadcast IP") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["hostIp"] ?: "",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("hostIp", newValue)
                }
            },
            label = { Text("NAS IP") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["username"] ?: "",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("username", newValue)
                }
            },
            label = { Text("SSH Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["sshPort"] ?: "22",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("sshPort", newValue)
                }
            },
            label = { Text("SSH Port") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["privateKey"] ?: "",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("privateKey", newValue)
                }
            },
            label = { Text("Private Key") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = config["shutdownCommand"] ?: "sudo /sbin/poweroff",
            onValueChange = { newValue ->
                scope.launch {
                    dataStore.update("shutdownCommand", newValue)
                }
            },
            label = { Text("Shutdown Command") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
