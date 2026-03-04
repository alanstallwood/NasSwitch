package com.alanstallwood.nasswitch

import android.content.Context
import com.alanstallwood.nasswitch.data.datastore.NasPreferencesDataStore
import com.alanstallwood.nasswitch.data.network.SshService
import com.alanstallwood.nasswitch.data.network.StatusService
import com.alanstallwood.nasswitch.data.network.WolService
import com.alanstallwood.nasswitch.domain.model.NasConfig
import com.alanstallwood.nasswitch.domain.repository.NasRepository
import com.alanstallwood.nasswitch.domain.repository.NasRepositoryImpl
import com.alanstallwood.nasswitch.domain.usecase.GetNasStatus
import com.alanstallwood.nasswitch.domain.usecase.ShutdownNas
import com.alanstallwood.nasswitch.domain.usecase.WakeNas
import com.alanstallwood.nasswitch.ui.model.NasViewModel

class AppContainer(context: Context) {

    // TEMP — replace later with DataStore config
    private val wolService = WolService()
    private val sshService = SshService()
    private val statusService = StatusService()
    
    private val dataStore = NasPreferencesDataStore(context)

    private val repository: NasRepository =
        NasRepositoryImpl(dataStore, wolService, sshService, statusService)

    private val wakeNas = WakeNas(repository)
    private val shutdownNas = ShutdownNas(repository)
    private val getNasStatus = GetNasStatus(repository)

    fun createViewModel(): NasViewModel =
        NasViewModel(wakeNas, shutdownNas, getNasStatus)

    fun getDataStore(): NasPreferencesDataStore = dataStore
}