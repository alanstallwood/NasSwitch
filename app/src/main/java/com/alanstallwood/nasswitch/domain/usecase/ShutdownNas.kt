package com.alanstallwood.nasswitch.domain.usecase

import com.alanstallwood.nasswitch.domain.repository.NasRepository

class ShutdownNas(
    private val repository: NasRepository
) {
    suspend operator fun invoke() = repository.shutdown()
}
