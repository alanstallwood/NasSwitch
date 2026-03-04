package com.alanstallwood.nasswitch.domain.repository

interface NasRepository {
    suspend fun wake()
    suspend fun shutdown()
    suspend fun isOnline(): Boolean
}