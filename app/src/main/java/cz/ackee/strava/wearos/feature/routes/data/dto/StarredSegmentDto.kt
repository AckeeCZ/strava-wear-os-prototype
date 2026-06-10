package cz.ackee.strava.wearos.feature.routes.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StarredSegmentDto(
    val id: Long,
    val name: String,
    @SerialName("pr_time") val prTime: Long? = null,
)
