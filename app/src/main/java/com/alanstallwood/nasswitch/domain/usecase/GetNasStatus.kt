package com.alanstallwood.nasswitch.domain.usecase

import com.alanstallwood.nasswitch.domain.repository.NasRepository

class GetNasStatus(
    private val repository: NasRepository
) {
    suspend operator fun invoke(): Boolean =
        repository.isOnline()
}