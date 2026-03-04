package com.alanstallwood.nasswitch.domain.model

data class NasConfig(
    val name: String,
    val macAddress: String,
    val broadcastIp: String,
    val hostIp: String,
    val sshPort: Int,
    val username: String,
    val privateKey: String,
    val shutdownCommand: String
)