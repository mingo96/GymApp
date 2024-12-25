package com.example.rutinapp.domain.addUseCases

import com.example.rutinapp.data.models.SetModel
import com.example.rutinapp.data.repositories.SetRepository
import javax.inject.Inject

class AddSetUseCase @Inject constructor(private val setRepository: SetRepository) {

    suspend operator fun invoke(set: SetModel):Int {
        return setRepository.insertSet(set.toEntity())
    }

}