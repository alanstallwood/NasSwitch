package com.alanstallwood.nasswitch.domain.repository

import android.content.Context
import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.NetworkUtils
import com.alanstallwood.nasswitch.data.network.PingService
import com.alanstallwood.nasswitch.data.network.SshService
import com.alanstallwood.nasswitch.data.network.StatusService
import com.alanstallwood.nasswitch.data.network.WolService
import com.alanstallwood.nasswitch.data.security.SecureKeyStore

object RepositoryFactory {

    fun create(context: Context): NasRepository {
        val dataStore = NasPreferencesDataStore(context)
        val secureKeyStore = SecureKeyStore(context)
        val wolService = WolService()
        val sshService = SshService()
        val statusService = StatusService()
        val networkUtils = NetworkUtils(context)

        return NasRepository(
            dataStore,
            secureKeyStore,
            statusService,
            sshService,
            wolService,
            networkUtils
        )
    }
}