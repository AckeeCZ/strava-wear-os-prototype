package cz.ackee.strava.wearos.feature.routes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class RoutesListViewModel(private val repository: RouteRepository) : ViewModel() {

    private val _state = MutableStateFlow<RoutesListState>(RoutesListState.Loading)
    val state: StateFlow<RoutesListState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: RoutesListIntent) {
        when (intent) {
            RoutesListIntent.Retry -> load()
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun load() {
        _state.value = RoutesListState.Loading
        viewModelScope.launch {
            _state.value = try {
                val summaries = repository.list().map { it.toSummary() }
                RoutesListState.Content(summaries)
            } catch (e: Throwable) {
                coroutineContext.ensureActive()
                RoutesListState.Error
            }
        }
    }

    private fun Route.toSummary(): RouteSummary = RouteSummary(
        id = id,
        name = name,
        distanceKm = distanceMeters / METERS_PER_KILOMETER,
        elevationGainM = elevationGainMeters.roundToInt(),
    )

    private companion object {
        const val METERS_PER_KILOMETER = 1000.0
    }
}
