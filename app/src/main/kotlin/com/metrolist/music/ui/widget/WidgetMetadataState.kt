package com.metrolist.music.ui.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.state.GlanceStateDefinition
import com.metrolist.music.models.WidgetMetadata
import com.metrolist.music.utils.dataStore
import com.metrolist.music.utils.get
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.IOException
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object WidgetMetadataState : GlanceStateDefinition<WidgetMetadata> {
    private const val DATASTORE_FILE_NAME = "widget_metadata_datastore" // Changed filename suffix
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    suspend fun setLoading(context: Context, isLoading: Boolean) = context.preferenceDataStore.edit { it[booleanPreferencesKey("isLoading")] = isLoading }
    fun isLoading(context: Context) = context.preferenceDataStore.data.catch { emit(emptyPreferences()) }.map { it[booleanPreferencesKey("isLoading")] ?: false }

    private val Context.preferenceDataStore by preferencesDataStore("widget_preference")

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
