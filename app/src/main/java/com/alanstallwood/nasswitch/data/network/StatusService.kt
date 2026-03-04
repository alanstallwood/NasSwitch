package com.alanstallwood.nasswitch.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class StatusService {

    suspend fun isOnline(
        host: String,
        port: Int = 22,
        timeoutMs: Int = 1000
    ): Boolean = withContext(Dispatchers.IO) {

        return@withContext try {
            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(host, port),
                    timeoutMs
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}