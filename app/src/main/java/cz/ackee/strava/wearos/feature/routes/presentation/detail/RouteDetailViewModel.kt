package cz.ackee.strava.wearos.feature.routes.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.usecase.GetRouteWithHighlightedSegmentsUseCase
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteDetailViewModel(
    private val id: Route.Id,
    private val getRouteWithHighlightedSegments: GetRouteWithHighlightedSegmentsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<RouteDetailState>(RouteDetailState.Loading)
    val state: StateFlow<RouteDetailState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: RouteDetailIntent) {
        when (intent) {
            RouteDetailIntent.Retry -> load()
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun load() {
        _state.value = RouteDetailState.Loading
        viewModelScope.launch {
            _state.value = try {
                val result = getRouteWithHighlightedSegments(id)
                RouteDetailState.Content(
                    route = result.route,
                    highlightedSegments = result.segments.filter { it.isStarred },
                    totalSegments = result.segments.size,
                )
            } catch (e: Throwable) {
                coroutineContext.ensureActive()
                RouteDetailState.Error
            }
        }
    }
}
