package com.mintocode.rutinapp.ui.floating

import com.mintocode.rutinapp.data.models.ExerciseModel
import com.mintocode.rutinapp.data.models.SetModel
import com.mintocode.rutinapp.data.models.WorkoutModel

/**
 * Singleton que actúa como puente de datos entre el ViewModel principal y el FloatingWorkoutService.
 *
 * Almacena una referencia al entrenamiento activo para que el servicio de overlay
 * pueda leer los ejercicios disponibles y crear sets sin depender del ViewModel directamente.
 * También rastrea el último set añadido para el temporizador de la burbuja y la
 * selección de ejercicio por defecto.
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

    /** Timestamp del último set añadido (para el temporizador de la burbuja). */
    @Volatile
    var lastSetTime: Long = 0L
        private set

    /** Índice del ejercicio seleccionado actualmente en el widget flotante. */
    @Volatile
    var selectedExerciseIndex: Int = 0

    /**
     * Establece el entrenamiento activo y el callback de creación de sets.
     *
     * @param workout Entrenamiento en curso
     * @param onAddSet Lambda que persiste el set y actualiza el estado del ViewModel
     */
    fun setActiveWorkout(workout: WorkoutModel, onAddSet: (SetModel) -> Unit) {
        activeWorkout = workout
        onSetAdded = onAddSet
        selectedExerciseIndex = findLastSetExerciseIndex(workout)
        lastSetTime = findLastSetTimestamp(workout)
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
     * Registra que se ha añadido un nuevo set, actualizando el timestamp.
     *
     * @param exerciseIndex Índice del ejercicio al que pertenece el set
     */
    fun recordSetAdded(exerciseIndex: Int) {
        lastSetTime = System.currentTimeMillis()
        selectedExerciseIndex = exerciseIndex
    }

    /**
     * Limpia el holder al finalizar o cancelar el entrenamiento.
     */
    fun clear() {
        activeWorkout = null
        onSetAdded = null
        lastSetTime = 0L
        selectedExerciseIndex = 0
    }

    /**
     * Devuelve la lista de ejercicios incluidos en el entrenamiento activo.
     *
     * @return Lista de nombres de ejercicios, vacía si no hay entrenamiento activo
     */
    fun getExerciseNames(): List<String> {
        return activeWorkout?.exercisesAndSets?.map { it.first.name } ?: emptyList()
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

    /**
     * Devuelve los sets del ejercicio en la posición indicada.
     *
     * @param index Posición del ejercicio
     * @return Lista de sets, vacía si inválido
     */
    fun getSetsForExercise(index: Int): List<SetModel> {
        val exercises = activeWorkout?.exercisesAndSets ?: return emptyList()
        return if (index in exercises.indices) exercises[index].second else emptyList()
    }

    /**
     * Devuelve el peso máximo registrado para el ejercicio en la posición indicada.
     *
     * @param index Posición del ejercicio
     * @return Peso máximo o 0.0 si no hay sets
     */
    fun getMaxWeightForExercise(index: Int): Double {
        return getSetsForExercise(index).maxOfOrNull { it.weight } ?: 0.0
    }

    /**
     * Devuelve el último set registrado para el ejercicio en la posición indicada.
     *
     * @param index Posición del ejercicio
     * @return Último SetModel o null si no hay sets
     */
    fun getLastSetForExercise(index: Int): SetModel? {
        return getSetsForExercise(index).lastOrNull()
    }

    /**
     * Busca el índice del ejercicio que tiene el set más reciente cronológicamente.
     *
     * @param workout Entrenamiento a inspeccionar
     * @return Índice del ejercicio con el último set, 0 si no hay sets
     */
    private fun findLastSetExerciseIndex(workout: WorkoutModel): Int {
        var latestTime = 0L
        var latestIndex = 0
        workout.exercisesAndSets.forEachIndexed { index, (_, sets) ->
            sets.forEach { set ->
                if (set.date.time > latestTime) {
                    latestTime = set.date.time
                    latestIndex = index
                }
            }
        }
        return latestIndex
    }

    /**
     * Busca el timestamp del set más reciente en el entrenamiento.
     *
     * @param workout Entrenamiento a inspeccionar
     * @return Timestamp del último set, 0L si no hay sets
     */
    private fun findLastSetTimestamp(workout: WorkoutModel): Long {
        var latest = 0L
        workout.exercisesAndSets.forEach { (_, sets) ->
            sets.forEach { set ->
                if (set.date.time > latest) latest = set.date.time
            }
        }
        return latest
    }
}
