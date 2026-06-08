package cz.ackee.strava.wearos.feature.routes.data.mapper

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import cz.ackee.strava.wearos.feature.routes.data.dto.PolylineMapDto
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteSegmentDto

internal fun latLngs(vararg pairs: Pair<Double, Double>): List<LatLng> =
    pairs.map { LatLng(it.first, it.second) }

@Suppress("LongParameterList")
internal fun routeSegmentDto(
    id: Long = 1L,
    name: String = "Segment",
    distance: Double = 100.0,
    averageGrade: Double = 0.0,
    maximumGrade: Double = 0.0,
    elevationHigh: Double = 100.0,
    elevationLow: Double = 50.0,
    climbCategory: Int = 0,
    startLatLng: LatLng = LatLng(0.0, 0.0),
    endLatLng: LatLng = LatLng(0.0, 0.0),
    city: String? = null,
    country: String? = null,
): RouteSegmentDto = RouteSegmentDto(
    id = id,
    name = name,
    distance = distance,
    averageGrade = averageGrade,
    maximumGrade = maximumGrade,
    elevationHigh = elevationHigh,
    elevationLow = elevationLow,
    climbCategory = climbCategory,
    startLatLng = startLatLng,
    endLatLng = endLatLng,
    city = city,
    country = country,
)

@Suppress("LongParameterList")
internal fun routeDto(
    polyline: List<LatLng>,
    segments: List<RouteSegmentDto> = emptyList(),
    detailedPolyline: List<LatLng>? = null,
    id: Long = 1L,
    name: String = "Route",
    distance: Double = 1000.0,
    elevationGain: Double = 50.0,
    estimatedMovingTime: Long = 3600L,
): RouteDto = RouteDto(
    id = id,
    name = name,
    distance = distance,
    elevationGain = elevationGain,
    estimatedMovingTime = estimatedMovingTime,
    map = PolylineMapDto(
        polyline = detailedPolyline?.let { PolyUtil.encode(it) },
        summaryPolyline = PolyUtil.encode(polyline),
    ),
    segments = segments,
)

internal fun loadAsset(path: String): String =
    object {}.javaClass.classLoader!!
        .getResourceAsStream(path)!!
        .bufferedReader()
        .readText()
