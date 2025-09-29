package com.metrolist.music.ui.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.glance.state.GlanceStateDefinition
import com.metrolist.music.models.WidgetMetadata
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.IOException
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object WidgetMetadataState : GlanceStateDefinition<WidgetMetadata> {
    private const val DATASTORE_FILE_NAME = "pixelPlayPlayerInfo_v1_json" // Changed filename suffix
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true // Important if some default values in PlayerInfo might be null initially
    }

    private val Context.widgetMetadataDataStore: DataStore<WidgetMetadata> by dataStore(
        fileName = DATASTORE_FILE_NAME,
        serializer = WidgetMetadataJsonSerializer(json) // Use new JSON serializer
    )

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<WidgetMetadata> = context.widgetMetadataDataStore

    override fun getLocation(context: Context, fileKey: String): File = File(context.filesDir, "datastore/${DATASTORE_FILE_NAME}")

    internal class WidgetMetadataJsonSerializer(private val json: Json) : Serializer<WidgetMetadata> {
        override val defaultValue: WidgetMetadata = WidgetMetadata() // Default instance of the data class

        override suspend fun readFrom(input: InputStream): WidgetMetadata {
            try {
                val string = input.bufferedReader().use { it.readText() }
                if (string.isBlank()) return defaultValue // Handle empty file case
                return json.decodeFromString<WidgetMetadata>(string)
            } catch (exception: SerializationException) {
                throw CorruptionException("Cannot read json.", exception)
            } catch (e: IOException) {
                throw CorruptionException("Cannot read json due to IO issue.", e)
            }
        }

        override suspend fun writeTo(t: WidgetMetadata, output: OutputStream) {
            output.bufferedWriter().use {
                it.write(json.encodeToString(t))
            }
        }
    }
}
