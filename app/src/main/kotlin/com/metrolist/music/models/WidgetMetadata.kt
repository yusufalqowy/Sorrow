package com.metrolist.music.models

import kotlinx.serialization.Serializable

@Serializable
data class WidgetMetadata(
    val id: String? = null,
    val title: String? = null,
    val artists: String? = null,
    val thumbnailUrl: String? = null,
    val thumbnailBitmapData: ByteArray? = null,
    val albumId: String? = null,
    val explicit: Boolean = false,
    val liked: Boolean = false,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val baseColor: ULong? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetMetadata

        if (explicit != other.explicit) return false
        if (liked != other.liked) return false
        if (isPlaying != other.isPlaying) return false
        if (baseColor != other.baseColor) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (artists != other.artists) return false
        if (thumbnailUrl != other.thumbnailUrl) return false
        if (!thumbnailBitmapData.contentEquals(other.thumbnailBitmapData)) return false
        if (albumId != other.albumId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + liked.hashCode()
        result = 31 * result + isPlaying.hashCode()
        result = 31 * result + baseColor.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (artists?.hashCode() ?: 0)
        result = 31 * result + (thumbnailUrl?.hashCode() ?: 0)
        result = 31 * result + (thumbnailBitmapData?.contentHashCode() ?: 0)
        result = 31 * result + (albumId?.hashCode() ?: 0)
        return result
    }


}
