package com.mintocode.rutinapp.domain.deleteUseCases

import com.mintocode.rutinapp.data.models.RoutineModel
import com.mintocode.rutinapp.data.repositories.RoutineRepository
import javax.inject.Inject

/**
 * Deletes a routine from the local database.
 *
 * @property routineRepository Repository handling routine persistence
 */
class DeleteRoutineUseCase @Inject constructor(private val routineRepository: RoutineRepository) {

    /**
     * Deletes the given routine.
     *
     * @param routineModel The routine to delete
     */
    suspend operator fun invoke(routineModel: RoutineModel) {
        routineRepository.deleteRoutine(routineModel.toEntity())
    }
}
