package com.example.rutinapp.domain.deleteUseCases

import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.repositories.SetRepository
import javax.inject.Inject

class DeleteSetUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(set:SetModel) {
        setRepository.deleteSet(set.toEntity())
    }


}