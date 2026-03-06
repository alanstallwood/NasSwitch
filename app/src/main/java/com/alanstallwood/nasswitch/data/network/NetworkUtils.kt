package com.alanstallwood.nasswitch.data.network

import android.content.Context
import android.net.ConnectivityManager
import java.net.Inet4Address
import java.net.InetAddress

class NetworkUtils(private val context: Context) {

    /**
     * Finds the broadcast address for the currently active Wi-Fi network.
     * This implementation avoids deprecated WifiManager.dhcpInfo where possible
     * and correctly handles different network interface configurations.
     */
    fun getBroadcastAddress(): String? {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return null
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork) ?: return null

            // Look through all link addresses to find the IPv4 broadcast
            for (linkAddress in linkProperties.linkAddresses) {
                val address = linkAddress.address
                if (address is Inet4Address) {
                    // We found an IPv4 address. Now we need the prefix length to calculate broadcast.
                    val prefixLength = linkAddress.prefixLength
                    return calculateBroadcastAddress(address, prefixLength)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun calculateBroadcastAddress(address: InetAddress, prefixLength: Int): String? {
        val ip = address.address
        val mask = getMask(prefixLength)
        val broadcast = ByteArray(4)
        for (i in 0..3) {
            broadcast[i] = (ip[i].toInt() or (mask[i].toInt().inv() and 0xFF)).toByte()
        }
        return InetAddress.getByAddress(broadcast).hostAddress
    }

    private fun getMask(prefixLength: Int): ByteArray {
        val mask = ByteArray(4)
        var bits = prefixLength
        for (i in 0..3) {
            if (bits >= 8) {
                mask[i] = 0xFF.toByte()
                bits -= 8
            } else if (bits > 0) {
                mask[i] = (0xFF shl (8 - bits)).toByte()
                bits = 0
            } else {
                mask[i] = 0
            }
        }
        return mask
    }
}
