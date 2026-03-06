package com.alanstallwood.nasswitch.domain.repository

import android.content.Context
import com.alanstallwood.nasswitch.domain.NasState

interface INasRepository {
    suspend fun wake()
    suspend fun shutdown()
    suspend fun isOnline(): Boolean
    suspend fun getState(): NasState
}