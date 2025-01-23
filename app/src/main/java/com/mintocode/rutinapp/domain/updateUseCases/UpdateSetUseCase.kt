package com.mintocode.rutinapp.domain.updateUseCases

import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.repositories.SetRepository
import javax.inject.Inject

class UpdateSetUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(set: SetModel) {
        setRepository.updateSet(set.toEntity())
    }

}