package com.mintocode.rutinapp.ui.floating

import android.util.Log
import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.models.WorkoutModel

/**
 * Singleton que actúa como puente de datos entre el ViewModel principal y el FloatingWorkoutService.
 *
 * Almacena una referencia al entrenamiento activo para que el servicio de overlay
 * pueda leer los ejercicios disponibles y crear sets sin depender del ViewModel directamente.
 */
object ActiveWorkoutHolder {

    /** Entrenamiento activo actual, null si no hay ninguno en curso. */
    @Volatile
    var activeWorkout: WorkoutModel? = null
        private set

    /** Callback para añadir un set desde el widget flotante. */
    @Volatile
    var onSetAdded: ((SetModel) -> Unit)? = null
        private set

    /**
     * Establece el entrenamiento activo y el callback de creación de sets.
     *
     * @param workout Entrenamiento en curso
     * @param onAddSet Lambda que persiste el set y actualiza el estado del ViewModel
     */
    fun setActiveWorkout(workout: WorkoutModel, onAddSet: (SetModel) -> Unit) {
        Log.d("FloatingWorkout", "setActiveWorkout: id=${workout.id}, exercises=${workout.exercisesAndSets.size}, names=${workout.exercisesAndSets.map { it.first.name }}")
        activeWorkout = workout
        onSetAdded = onAddSet
    }

    /**
     * Actualiza la referencia al workout activo (por ejemplo cuando se añaden ejercicios).
     *
     * @param workout Workout actualizado
     */
    fun updateWorkout(workout: WorkoutModel) {
        activeWorkout = workout
    }

    /**
     * Limpia el holder al finalizar o cancelar el entrenamiento.
     */
    fun clear() {
        Log.d("FloatingWorkout", "clear() called, was workout=${activeWorkout?.id}")
        activeWorkout = null
        onSetAdded = null
    }

    /**
     * Devuelve la lista de ejercicios incluidos en el entrenamiento activo.
     *
     * @return Lista de nombres de ejercicios, vacía si no hay entrenamiento activo
     */
    fun getExerciseNames(): List<String> {
        val names = activeWorkout?.exercisesAndSets?.map { it.first.name } ?: emptyList()
        Log.d("FloatingWorkout", "getExerciseNames: workout=${activeWorkout?.id}, count=${names.size}, names=$names")
        return names
    }

    /**
     * Devuelve el ExerciseModel en la posición indicada dentro del entrenamiento activo.
     *
     * @param index Posición del ejercicio en la lista
     * @return ExerciseModel o null si el índice es inválido
     */
    fun getExerciseAt(index: Int): ExerciseModel? {
        val exercises = activeWorkout?.exercisesAndSets ?: return null
        return if (index in exercises.indices) exercises[index].first else null
    }
}
