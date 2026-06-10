package cz.ackee.strava.wearos.feature.routes.data.serializer

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LatLngSerializer : KSerializer<LatLng> {

    private val delegate = ListSerializer(Double.serializer())

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: LatLng) {
        delegate.serialize(encoder, listOf(value.latitude, value.longitude))
    }

    override fun deserialize(decoder: Decoder): LatLng {
        val coords = delegate.deserialize(decoder)
        require(coords.size == 2) { "Expected [lat, lng] but got $coords" }
        return LatLng(coords[0], coords[1])
    }
}
