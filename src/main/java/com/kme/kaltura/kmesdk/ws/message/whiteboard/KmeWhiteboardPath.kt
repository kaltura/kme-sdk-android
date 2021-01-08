package com.kme.kaltura.kmesdk.ws.message.whiteboard

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardShapeType


data class KmeWhiteboardPath(
    @SerializedName("name") val name: String? = null,
    @SerializedName("applyMatrix") val applyMatrix: Boolean? = null,
    @SerializedName("matrix") val matrix: FloatArray? = null,
    @SerializedName("size") val size: FloatArray? = null,
    @SerializedName("pivot") val pivot: FloatArray? = null,
    @SerializedName("radius") val radius: FloatArray? = null,
    @SerializedName("strokeColor") val strokeColor: FloatArray? = null,
    @SerializedName("fillColor") val fillColor: FloatArray? = null,
    @SerializedName("strokeCap") val strokeCap: Cap? = null,
    @SerializedName("strokeWidth") val strokeWidth: Int,
    @SerializedName("opacity") val opacity: Float? = null,
    @SerializedName("blendMode") val blendMode: BlendMode? = null,
    @SerializedName("data") val data: Data? = null,
    @SerializedName("segments") val segments: List<List<Any>>? = null,
    @SerializedName("type") val type: KmeWhiteboardShapeType? = null,
    @SerializedName("fontSize") val fontSize: Int? = null,
    @SerializedName("leading") val leading: Int? = null,
    @SerializedName("rectangle") val rectangle: FloatArray? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("closed") val closed: Boolean? = null,
    var childrenPath: KmeWhiteboardPath? = null,
    var pathType: String? = null
) {

    enum class Cap {
        @SerializedName("round")
        ROUND,

        @SerializedName("butt")
        BUTT,

        @SerializedName("square")
        SQUARE
    }

    enum class BlendMode {
        @SerializedName("multiply")
        MULTIPLY,

        @SerializedName("destination-out")
        DESTINATION_OUT
    }


    data class Data(
        @SerializedName("user_id") val userId: Long? = null,
        @SerializedName("user_name") val userName: String? = null,
        @SerializedName("isUrl") val isUrl: Boolean
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KmeWhiteboardPath

        if (name != other.name) return false
        if (applyMatrix != other.applyMatrix) return false
        if (matrix != null) {
            if (other.matrix == null) return false
            if (!matrix.contentEquals(other.matrix)) return false
        } else if (other.matrix != null) return false
        if (size != null) {
            if (other.size == null) return false
            if (!size.contentEquals(other.size)) return false
        } else if (other.size != null) return false
        if (radius != null) {
            if (other.radius == null) return false
            if (!radius.contentEquals(other.radius)) return false
        } else if (other.radius != null) return false
        if (strokeColor != null) {
            if (other.strokeColor == null) return false
            if (!strokeColor.contentEquals(other.strokeColor)) return false
        } else if (other.strokeColor != null) return false
        if (fillColor != null) {
            if (other.fillColor == null) return false
            if (!fillColor.contentEquals(other.fillColor)) return false
        } else if (other.fillColor != null) return false
        if (strokeCap != other.strokeCap) return false
        if (strokeWidth != other.strokeWidth) return false
        if (opacity != other.opacity) return false
        if (blendMode != other.blendMode) return false
        if (data != other.data) return false
        if (segments != other.segments) return false
        if (type != other.type) return false
        if (closed != other.closed) return false
        if (childrenPath != other.childrenPath) return false
        if (pathType != other.pathType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (applyMatrix?.hashCode() ?: 0)
        result = 31 * result + (matrix?.contentHashCode() ?: 0)
        result = 31 * result + (size?.contentHashCode() ?: 0)
        result = 31 * result + (radius?.contentHashCode() ?: 0)
        result = 31 * result + (strokeColor?.contentHashCode() ?: 0)
        result = 31 * result + (fillColor?.contentHashCode() ?: 0)
        result = 31 * result + (strokeCap?.hashCode() ?: 0)
        result = 31 * result + strokeWidth
        result = 31 * result + (opacity?.hashCode() ?: 0)
        result = 31 * result + (blendMode?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (segments?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (closed?.hashCode() ?: 0)
        result = 31 * result + (childrenPath?.hashCode() ?: 0)
        result = 31 * result + (pathType?.hashCode() ?: 0)
        return result
    }


}
