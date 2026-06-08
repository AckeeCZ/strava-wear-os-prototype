package cz.ackee.strava.wearos.feature.routes.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteMapViewModel(
    private val id: Route.Id,
    private val repository: RouteRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RouteMapState>(RouteMapState.Loading)
    val state: StateFlow<RouteMapState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: RouteMapIntent) {
        when (intent) {
            RouteMapIntent.Retry -> load()
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun load() {
        _state.value = RouteMapState.Loading
        viewModelScope.launch {
            _state.value = try {
                RouteMapState.Content(repository.get(id))
            } catch (e: Throwable) {
                coroutineContext.ensureActive()
                RouteMapState.Error
            }
        }
    }
}
