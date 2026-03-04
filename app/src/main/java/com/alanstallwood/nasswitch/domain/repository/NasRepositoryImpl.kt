package com.alanstallwood.nasswitch.domain.repository

import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.SshService
import com.alanstallwood.nasswitch.data.network.StatusService
import com.alanstallwood.nasswitch.data.network.WolService
import com.alanstallwood.nasswitch.domain.model.NasConfig
import kotlinx.coroutines.flow.first

class NasRepositoryImpl(
    private val dataStore: NasPreferencesDataStore,
    private val wolService: WolService,
    private val sshService: SshService,
    private val statusService: StatusService
) : NasRepository {

    private suspend fun getConfig(): NasConfig {
        val map = dataStore.nasConfigFlow.first() // collect once
        return NasConfig(
            name = map["name"] ?: "NAS",
            macAddress = map["macAddress"] ?: "",
            broadcastIp = map["broadcastIp"] ?: "",
            hostIp = map["hostIp"] ?: "",
            sshPort = map["sshPort"]?.toIntOrNull() ?: 22,
            username = map["username"] ?: "",
            privateKey = map["privateKey"] ?: "",
            shutdownCommand = map["shutdownCommand"] ?: "sudo /sbin/poweroff"
        )
    }

    override suspend fun wake() {
        val cfg = getConfig()
        wolService.sendWakePacket(cfg.macAddress, cfg.broadcastIp)
    }

    override suspend fun shutdown() {
        val cfg = getConfig()
        sshService.executeShutdown(
            host = cfg.hostIp,
            port = cfg.sshPort,
            username = cfg.username,
            privateKey = cfg.privateKey,
            command = cfg.shutdownCommand
        )
    }

    override suspend fun isOnline(): Boolean {
        val cfg = getConfig()
        return statusService.isOnline(cfg.hostIp, cfg.sshPort)
    }
}