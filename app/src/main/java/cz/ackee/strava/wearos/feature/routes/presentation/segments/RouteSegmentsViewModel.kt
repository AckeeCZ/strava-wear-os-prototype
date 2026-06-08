package cz.ackee.strava.wearos.feature.routes.presentation.segments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.usecase.GetRouteWithHighlightedSegmentsUseCase
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteSegmentsViewModel(
    private val id: Route.Id,
    private val getRouteWithHighlightedSegments: GetRouteWithHighlightedSegmentsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<RouteSegmentsState>(RouteSegmentsState.Loading)
    val state: StateFlow<RouteSegmentsState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: RouteSegmentsIntent) {
        when (intent) {
            RouteSegmentsIntent.Retry -> load()
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun load() {
        _state.value = RouteSegmentsState.Loading
        viewModelScope.launch {
            _state.value = try {
                val result = getRouteWithHighlightedSegments(id)
                RouteSegmentsState.Content(result.segments)
            } catch (e: Throwable) {
                coroutineContext.ensureActive()
                RouteSegmentsState.Error
            }
        }
    }
}
