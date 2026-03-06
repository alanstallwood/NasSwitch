package com.alanstallwood.nasswitch.data.network

import java.net.InetAddress

class PingService {

    fun isReachable(host: String): Boolean {
        return try {
            val address = InetAddress.getByName(host)
            address.isReachable(2000)
        } catch (e: Exception) {
            false
        }
    }
}