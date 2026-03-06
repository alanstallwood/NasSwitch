package com.alanstallwood.nasswitch.domain.repository

import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.NetworkUtils
import com.alanstallwood.nasswitch.data.network.StatusService
import com.alanstallwood.nasswitch.data.network.SshService
import com.alanstallwood.nasswitch.data.network.WolService
import com.alanstallwood.nasswitch.data.security.SecureKeyStore
import com.alanstallwood.nasswitch.domain.NasState
import com.alanstallwood.nasswitch.domain.model.NasConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NasRepository(
    private val dataStore: NasPreferencesDataStore,
    private val secureKeyStore: SecureKeyStore,
    private val statusService: StatusService,
    private val sshService: SshService,
    private val wolService: WolService,
    private val networkUtils: NetworkUtils
) : INasRepository {


    override suspend fun getState(): NasState = withContext(Dispatchers.IO) {
        val config = getConfig()
        val online = statusService.isOnline(config.hostIp, config.sshPort)
        NasState(online)
    }

    private suspend fun getConfig(): NasConfig {

        val map = dataStore.nasConfigFlow.first() // collect once

        val broadcastIp =
            if (map["broadcastIp"].isNullOrBlank())
                networkUtils.getBroadcastAddress()
            else
                map["broadcastIp"]!!

        return NasConfig(
            name = map["name"] ?: "NAS",
            macAddress = map["macAddress"] ?: "",
            broadcastIp = broadcastIp ?: "",
            hostIp = map["hostIp"] ?: "",
            sshPort = map["sshPort"]?.toIntOrNull() ?: 22,
            username = map["username"] ?: "",
            privateKey = secureKeyStore.getPrivateKey() ?: "",
            shutdownCommand = map["shutdownCommand"] ?: "sudo /sbin/poweroff"
        )
    }

    override suspend fun wake() = withContext(Dispatchers.IO) {
        val cfg = getConfig()
        return@withContext wolService.sendWakePacket(cfg.macAddress, cfg.broadcastIp)
    }

    override suspend fun shutdown() = withContext(Dispatchers.IO) {
        val config = getConfig()

        return@withContext sshService.executeShutdown(
            config.hostIp, config.sshPort, config.username, config.privateKey, config.shutdownCommand
        )
    }

    override suspend fun isOnline(): Boolean {
        val cfg = getConfig()
        return statusService.isOnline(cfg.hostIp, cfg.sshPort)
    }
}
