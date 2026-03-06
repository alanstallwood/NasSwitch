package com.alanstallwood.nasswitch.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.keyprovider.KeyProvider

class SshService {

    suspend fun executeShutdown(
        host: String,
        port: Int,
        username: String,
        privateKey: String,
        command: String
    ) = withContext(Dispatchers.IO) {

        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier()) // Replace later with proper verifier

        try {
            ssh.connect(host, port)

            val keyProvider: KeyProvider =
                ssh.loadKeys(privateKey, null, null)

            ssh.authPublickey(username, keyProvider)

            val session = ssh.startSession()
            session.use {
                val cmd = it.exec(command)
                cmd.join() // wait for completion
            }

        }
        catch(it: Exception) {
            println("Error: ${it.message}")
        }
        finally {
            ssh.disconnect()
        }
    }
}