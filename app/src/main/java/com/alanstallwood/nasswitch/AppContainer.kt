package com.alanstallwood.nasswitch

import android.content.Context
import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.NetworkUtils
import com.alanstallwood.nasswitch.data.network.StatusService
import com.alanstallwood.nasswitch.data.network.SshService
import com.alanstallwood.nasswitch.data.network.WolService
import com.alanstallwood.nasswitch.data.security.SecureKeyStore
import com.alanstallwood.nasswitch.domain.repository.NasRepository
import com.alanstallwood.nasswitch.domain.usecase.GetNasStatus
import com.alanstallwood.nasswitch.domain.usecase.ShutdownNas
import com.alanstallwood.nasswitch.domain.usecase.WakeNas
import com.alanstallwood.nasswitch.ui.model.NasViewModel

class AppContainer(context: Context) {

    private val wolService = WolService()
    private val sshService = SshService()
    private val statusService = StatusService()
    
    private val dataStore = NasPreferencesDataStore(context)
    private val secureKeyStore = SecureKeyStore(context)
    private val networkUtils = NetworkUtils(context)


    val repository: NasRepository =
        NasRepository(dataStore, secureKeyStore, statusService, sshService, wolService, networkUtils)

    private val wakeNas = WakeNas(repository)
    private val shutdownNas = ShutdownNas(repository)
    private val getNasStatus = GetNasStatus(repository)

    fun createViewModel(): NasViewModel =
        NasViewModel(wakeNas, shutdownNas, getNasStatus)

    fun getDataStore(): NasPreferencesDataStore = dataStore

    fun getSecureKeyStore() = secureKeyStore

    fun getNetworkUtils() = networkUtils
}
