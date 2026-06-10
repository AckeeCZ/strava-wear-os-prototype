package cz.ackee.strava.wearos.feature.routes.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.model.Segment
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SegmentMapViewModel(
    private val routeId: Route.Id,
    private val segmentId: Segment.Id,
    private val repository: RouteRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SegmentMapState>(SegmentMapState.Loading)
    val state: StateFlow<SegmentMapState> = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: SegmentMapIntent) {
        when (intent) {
            SegmentMapIntent.Retry -> load()
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun load() {
        _state.value = SegmentMapState.Loading
        viewModelScope.launch {
            _state.value = try {
                val segment = repository.get(routeId).segments.firstOrNull { it.id == segmentId }
                if (segment != null) SegmentMapState.Content(segment) else SegmentMapState.Error
            } catch (e: Throwable) {
                coroutineContext.ensureActive()
                SegmentMapState.Error
            }
        }
    }
}
