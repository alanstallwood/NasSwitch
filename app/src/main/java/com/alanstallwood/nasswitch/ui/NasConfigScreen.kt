package com.alanstallwood.nasswitch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.NetworkUtils
import com.alanstallwood.nasswitch.data.security.SecureKeyStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasConfigScreen(
    dataStore: NasPreferencesDataStore,
    secureKeyStore: SecureKeyStore,
    networkUtils: NetworkUtils,
    onBack: () -> Unit
) {
    val configFlow = dataStore.nasConfigFlow.collectAsState(initial = emptyMap())
    val config = configFlow.value
    val scope = rememberCoroutineScope()

    // Local state for fields
    var macAddress by remember { mutableStateOf(config["macAddress"] ?: "") }
    var hostIp by remember { mutableStateOf(config["hostIp"] ?: "") }
    var broadcastIp by remember { mutableStateOf(config["broadcastIp"] ?: "") }
    var username by remember { mutableStateOf(config["username"] ?: "") }
    var sshPort by remember { mutableStateOf(config["sshPort"] ?: "22") }
    var shutdownCommand by remember { mutableStateOf(config["shutdownCommand"] ?: "sudo /sbin/poweroff") }

    var privateKey by remember { mutableStateOf("") }
    var keyVisible by remember { mutableStateOf(false) }
    var keyError by remember { mutableStateOf<String?>(null) }

    // Sync local state with DataStore when config changes
    LaunchedEffect(config) {
        macAddress = config["macAddress"] ?: ""
        hostIp = config["hostIp"] ?: ""
        broadcastIp = config["broadcastIp"]?.takeIf { it.isNotBlank() }
            ?: networkUtils.getBroadcastAddress()
                    ?: ""
        username = config["username"] ?: ""
        sshPort = config["sshPort"] ?: "22"
        shutdownCommand = config["shutdownCommand"] ?: "sudo /sbin/poweroff"
        privateKey = secureKeyStore.getPrivateKey() ?: ""
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NAS Configuration") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // MAC Address
            OutlinedTextField(
                value = macAddress,
                onValueChange = { input ->
                    val filtered = input.uppercase().filter { it.isDigit() || it in "ABCDEF:" }
                    macAddress = filtered
                    scope.launch { dataStore.update("macAddress", macAddress) }
                },
                label = { Text("MAC Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Host IP
            OutlinedTextField(
                value = hostIp,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        hostIp = input
                        scope.launch { dataStore.update("hostIp", input) }
                    }
                },
                label = { Text("NAS IP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Broadcast IP (editable)
            OutlinedTextField(
                value = broadcastIp,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        broadcastIp = input
                        scope.launch { dataStore.update("broadcastIp", input) }
                    }
                },
                label = { Text("Broadcast IP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SSH Username
            OutlinedTextField(
                value = username,
                onValueChange = { input ->
                    username = input
                    scope.launch { dataStore.update("username", input) }
                },
                label = { Text("SSH Username") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SSH Port
            OutlinedTextField(
                value = sshPort,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        sshPort = input
                        scope.launch { dataStore.update("sshPort", input) }
                    }
                },
                label = { Text("SSH Port") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SSH Private Key
            OutlinedTextField(
                value = privateKey,
                onValueChange = {
                    privateKey = it
                    keyError = null
                },
                label = { Text("SSH Private Key") },
                visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = keyError != null,
                colors = textFieldColors,
                trailingIcon = {
                    Row {
                        TextButton(onClick = { keyVisible = !keyVisible }) {
                            Text(if (keyVisible) "Hide" else "Show")
                        }
                        TextButton(onClick = {
                            if (isValidPrivateKey(privateKey)) {
                                secureKeyStore.savePrivateKey(privateKey)
                                keyError = null
                            } else {
                                keyError = "Invalid private key"
                            }
                        }) {
                            Text("Save")
                        }
                    }
                }
            )
            keyError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            // Shutdown Command
            OutlinedTextField(
                value = shutdownCommand,
                onValueChange = { input ->
                    shutdownCommand = input
                    scope.launch { dataStore.update("shutdownCommand", input) }
                },
                label = { Text("Shutdown Command") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Back button
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

fun isValidPrivateKey(key: String): Boolean {
    return key.contains("BEGIN") && key.contains("PRIVATE KEY")
}

