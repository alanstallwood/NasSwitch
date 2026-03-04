package com.alanstallwood.nasswitch.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class WolService {

    suspend fun sendWakePacket(
        macAddress: String,
        broadcastIp: String,
        port: Int = 9
    ) = withContext(Dispatchers.IO) {

        val macBytes = parseMac(macAddress)

        val bytes = ByteArray(6 + 16 * macBytes.size)

        // 6 bytes of 0xFF
        for (i in 0 until 6) {
            bytes[i] = 0xFF.toByte()
        }

        // Repeat MAC 16 times
        for (i in 6 until bytes.size step macBytes.size) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
        }

        val address = InetAddress.getByName(broadcastIp)
        val packet = DatagramPacket(bytes, bytes.size, address, port)

        DatagramSocket().use { socket ->
            socket.broadcast = true
            socket.send(packet)
        }
    }

    private fun parseMac(mac: String): ByteArray {
        val clean = mac.replace(":", "").replace("-", "")
        require(clean.length == 12) { "Invalid MAC address format" }

        return ByteArray(6) { i ->
            clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}