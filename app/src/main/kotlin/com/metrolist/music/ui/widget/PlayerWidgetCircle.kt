package com.metrolist.music.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ProgressIndicatorDefaults
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProviders
import androidx.glance.color.colorProviders
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.DynamicScheme
import com.materialkolor.ktx.themeColorOrNull
import com.materialkolor.ktx.toDynamicScheme
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.scheme.DynamicScheme
import com.metrolist.music.MainActivity
import com.metrolist.music.R
import com.metrolist.music.models.WidgetMetadata
import kotlinx.coroutines.delay
import timber.log.Timber

@SuppressLint("RestrictedApi")
class PlayerWidgetCircle : GlanceAppWidget() {
    private object AlbumArtBitmapCache {
        private const val CACHE_SIZE_BYTES = 4 * 1024 * 1024 // 4 MiB
        private val lruCache = object : LruCache<String, Bitmap>(CACHE_SIZE_BYTES) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount
            }
        }

        fun getBitmap(key: String): Bitmap? = lruCache.get(key)

        fun putBitmap(key: String, bitmap: Bitmap) {
            if (getBitmap(key) == null) {
                lruCache.put(key, bitmap)
            }
        }

        fun getKey(byteArray: ByteArray): String {
            return byteArray.contentHashCode().toString()
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current
            val metadata = currentState<WidgetMetadata>()
            var colors: ColorProviders? by remember { mutableStateOf(null) }
            LaunchedEffect(metadata.baseColor) {
                if (metadata.baseColor != null) {
                    colors = getColorProviders(context, Color(metadata.baseColor))
                }
            }
            GlanceTheme(
                colors = colors ?: GlanceTheme.colors
            ) {
                PlayerWidgetContent(metadata)
            }
        }
    }

    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition: GlanceStateDefinition<WidgetMetadata> = WidgetMetadataState

    @Composable
    private fun PlayerWidgetContent(metadata: WidgetMetadata) {
        Box(contentAlignment = Alignment.Center, modifier = GlanceModifier.fillMaxSize()) {
            OneByOneWidgetLayout(
                metadata = metadata,
                modifier = GlanceModifier.size(LocalSize.current.width),
                bgCornerRadius = LocalSize.current.width,
            )
        }
    }

    @Composable
    fun OneByOneWidgetLayout(
        modifier: GlanceModifier,
        bgCornerRadius: Dp,
        metadata: WidgetMetadata
    ) {
        val bitmapData = metadata.thumbnailBitmapData
        var isButtonVisible by remember { mutableStateOf(bitmapData == null) }
        val imageProvider = getBitmap(bitmapData, LocalContext.current, LocalSize.current)?.let { ImageProvider(it) }

        LaunchedEffect(isButtonVisible) {
            if (isButtonVisible && bitmapData != null) {
                delay(6000)
                isButtonVisible = false
            }
        }

        val onClick = action { isButtonVisible = !isButtonVisible }
        val playPauseClick = if (metadata.id == null) actionStartActivity<MainActivity>() else actionRunCallback<PlayerControlActionCallback>(actionParametersOf(PlayerActions.key to PlayerActions.PLAY_PAUSE))

        Box(
            modifier = modifier.clickable(rippleOverride = -1, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageProvider != null) {
                Image(modifier = GlanceModifier.cornerRadius(bgCornerRadius).fillMaxSize(), provider = imageProvider, contentDescription = null, contentScale = ContentScale.Crop)
            }
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ){
                val appIconSize = LocalSize.current.width.div(4).minus(8.dp)
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = appIconSize.div(4), end = appIconSize.div(4)),
                    horizontalAlignment = Alignment.End
                ) {
                    Image(modifier = GlanceModifier.background(GlanceTheme.colors.primary).size(appIconSize).padding(8.dp).cornerRadius(appIconSize).clickable(onClick = actionStartActivity<MainActivity>()), provider = ImageProvider(R.drawable.small_icon), contentDescription = null, colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary))
                }

                Row(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (metadata.isLoading && !metadata.isPlaying){
                        CircularProgressIndicator(modifier = GlanceModifier.size(appIconSize), color = GlanceTheme.colors.primary)
                    }
                }
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ){
                    val size = LocalSize.current.width.div(2).minus(32.dp)
                    ButtonGlance(
                        iconResource = if (metadata.isPlaying) R.drawable.pause else R.drawable.play,
                        cornerRadius = if (metadata.isPlaying) 16.dp else size,
                        backgroundColor = if (metadata.isPlaying) GlanceTheme.colors.primary else GlanceTheme.colors.secondaryContainer,
                        iconColor = if (metadata.isPlaying) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSecondaryContainer,
                        size = size,
                        iconSize = 32.dp,
                        onClick = playPauseClick
                    )
                }
            }

        }
    }

    @Composable
    fun ButtonGlance(
        modifier: GlanceModifier = GlanceModifier,
        @DrawableRes iconResource: Int,
        size: Dp = 40.dp,
        cornerRadius: Dp = size,
        iconSize: Dp = 20.dp,
        backgroundColor: ColorProvider = GlanceTheme.colors.secondaryContainer,
        iconColor: ColorProvider = GlanceTheme.colors.onSecondaryContainer,
        onClick: Action
    ) {
        val context = LocalContext.current
        val backgroundColor = backgroundColor.getColor(context)
        Box(
            modifier = modifier
                .size(size)
                .background(backgroundColor)
                .cornerRadius(if (cornerRadius == size) size else cornerRadius)
                .clickable(onClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(iconResource),
                contentDescription = "",
                modifier = GlanceModifier.size(iconSize),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }

    fun getBitmap(bitmapData: ByteArray?, context: Context, size: DpSize): Bitmap? {
        val resource = context.resources
        return bitmapData?.let { data ->
            val cacheKey = AlbumArtBitmapCache.getKey(data)
            var bitmap = AlbumArtBitmapCache.getBitmap(cacheKey)

            if (bitmap == null) {
                try {
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeByteArray(data, 0, data.size, options)

                    val imageHeight = options.outHeight
                    val imageWidth = options.outWidth
                    var inSampleSize = 1

                    // Determine target size in pixels
                    val targetWidthPx: Int
                    val targetHeightPx: Int
                    with(resource.displayMetrics) {
                        targetWidthPx = (size.width.value * density).toInt()
                        targetHeightPx = (size.height.value * density).toInt()
                    }

                    if (imageHeight > targetHeightPx || imageWidth > targetWidthPx) {
                        val halfHeight: Int = imageHeight / 2
                        val halfWidth: Int = imageWidth / 2
                        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                        // height and width larger than the requested height and width.
                        while (halfHeight / inSampleSize >= targetHeightPx && halfWidth / inSampleSize >= targetWidthPx) {
                            inSampleSize *= 2
                        }
                    }
                    Timber.e("Calculated inSampleSize: $inSampleSize")

                    options.inSampleSize = inSampleSize
                    options.inJustDecodeBounds = false
                    val sampledBitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)

                    if (sampledBitmap == null) {
                        Timber.e("BitmapFactory.decodeByteArray returned null after sampling.")
                        return@let null
                    }
                    bitmap = sampledBitmap
                    bitmap.let { AlbumArtBitmapCache.putBitmap(cacheKey, it) }

                } catch (e: Exception) {
                    Timber.e(e, "Error decoding or scaling bitmap: ${e.message}")
                    bitmap = null
                }
            }
            bitmap
        }
    }

    fun getColorProviders(context: Context, baseColor: Color): ColorProviders {
        val isDarkTheme = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val scheme = baseColor.toDynamicScheme(
            isDark = isDarkTheme,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = PaletteStyle.TonalSpot
        )

        return colorProviders(
            widgetBackground = ColorProvider(Color.Unspecified),
            primary = ColorProvider(Color(scheme.primary)),
            onPrimary = ColorProvider(Color(scheme.onPrimary)),
            primaryContainer = ColorProvider(Color(scheme.primaryContainer)),
            onPrimaryContainer = ColorProvider(Color(scheme.onPrimaryContainer)),
            secondary = ColorProvider(Color(scheme.secondary)),
            onSecondary = ColorProvider(Color(scheme.onSecondary)),
            secondaryContainer = ColorProvider(Color(scheme.secondaryContainer)),
            onSecondaryContainer = ColorProvider(Color(scheme.onSecondaryContainer)),
            tertiary = ColorProvider(Color(scheme.tertiary)),
            onTertiary = ColorProvider(Color(scheme.onTertiary)),
            tertiaryContainer = ColorProvider(Color(scheme.tertiaryContainer)),
            onTertiaryContainer = ColorProvider(Color(scheme.onTertiaryContainer)),
            error = ColorProvider(Color(scheme.error)),
            errorContainer = ColorProvider(Color(scheme.errorContainer)),
            onError = ColorProvider(Color(scheme.onError)),
            onErrorContainer = ColorProvider(Color(scheme.onErrorContainer)),
            background = ColorProvider(Color(scheme.background)),
            onBackground = ColorProvider(Color(scheme.onBackground)),
            surface = ColorProvider(Color(scheme.surface)),
            onSurface = ColorProvider(Color(scheme.onSurface)),
            surfaceVariant = ColorProvider(Color(scheme.surfaceVariant)),
            onSurfaceVariant = ColorProvider(Color(scheme.onSurfaceVariant)),
            outline = ColorProvider(Color(scheme.outline)),
            inverseOnSurface = ColorProvider(Color(scheme.inverseOnSurface)),
            inverseSurface = ColorProvider(Color(scheme.inverseSurface)),
            inversePrimary = ColorProvider(Color(scheme.inversePrimary)),
        )
    }
}
