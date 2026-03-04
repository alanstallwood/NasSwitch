package com.alanstallwood.nasswitch.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NasPreferencesDataStore(context: Context) {

    private val Context.dataStore by preferencesDataStore("nas_config")

    private val dataStore = context.dataStore

    // Keys
    private val MAC_ADDRESS = stringPreferencesKey("mac_address")
    private val BROADCAST_IP = stringPreferencesKey("broadcast_ip")
    private val HOST_IP = stringPreferencesKey("host_ip")
    private val SSH_PORT = intPreferencesKey("ssh_port")
    private val USERNAME = stringPreferencesKey("username")
    private val PRIVATE_KEY = stringPreferencesKey("private_key")
    private val SHUTDOWN_CMD = stringPreferencesKey("shutdown_cmd")

    // Read as Flow
    val nasConfigFlow: Flow<Map<String, String>> = dataStore.data.map { prefs ->
        mapOf(
            "macAddress" to (prefs[MAC_ADDRESS] ?: ""),
            "broadcastIp" to (prefs[BROADCAST_IP] ?: ""),
            "hostIp" to (prefs[HOST_IP] ?: ""),
            "sshPort" to (prefs[SSH_PORT]?.toString() ?: "22"),
            "username" to (prefs[USERNAME] ?: ""),
            "privateKey" to (prefs[PRIVATE_KEY] ?: ""),
            "shutdownCommand" to (prefs[SHUTDOWN_CMD] ?: "sudo /sbin/poweroff")
        )
    }

    // Update a value
    suspend fun update(key: String, value: String) {
        dataStore.edit { prefs ->
            when (key) {
                "macAddress" -> prefs[MAC_ADDRESS] = value
                "broadcastIp" -> prefs[BROADCAST_IP] = value
                "hostIp" -> prefs[HOST_IP] = value
                "sshPort" -> prefs[SSH_PORT] = value.toIntOrNull() ?: 22
                "username" -> prefs[USERNAME] = value
                "privateKey" -> prefs[PRIVATE_KEY] = value
                "shutdownCommand" -> prefs[SHUTDOWN_CMD] = value
            }
        }
    }
}