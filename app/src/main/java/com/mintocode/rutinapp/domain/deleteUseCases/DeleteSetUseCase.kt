package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.repositories.SetRepository
import javax.inject.Inject

class DeleteSetUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(set: SetModel) {
        setRepository.deleteSet(set.toEntity())
    }


}