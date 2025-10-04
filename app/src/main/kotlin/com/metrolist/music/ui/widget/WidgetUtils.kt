package com.metrolist.music.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.glance.LocalContext
import androidx.glance.color.ColorProviders
import androidx.glance.color.colorProviders
import androidx.glance.unit.ColorProvider
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.toDynamicScheme

@Composable
fun animatedColorProviders(
    colorProviders: ColorProviders,
    animationSpec: @Composable Transition.Segment<ColorProviders>.() -> FiniteAnimationSpec<Color> = { spring() },
    label: String = "animatedColorProviders",
): ColorProviders {
    val transition = updateTransition(targetState = colorProviders, label = label)
    val context = LocalContext.current

    val primary by transition.animateColor(
        label = "color_primary",
        targetValueByState = { it.primary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val primaryContainer by transition.animateColor(
        label = "color_primaryContainer",
        targetValueByState = { it.primaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val secondary by transition.animateColor(
        label = "color_secondary",
        targetValueByState = { it.secondary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val secondaryContainer by transition.animateColor(
        label = "color_secondaryContainer",
        targetValueByState = { it.secondaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val tertiary by transition.animateColor(
        label = "color_tertiary",
        targetValueByState = { it.tertiary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val tertiaryContainer by transition.animateColor(
        label = "color_tertiaryContainer",
        targetValueByState = { it.tertiaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val background by transition.animateColor(
        label = "color_background",
        targetValueByState = { it.background.getColor(context) },
        transitionSpec = animationSpec,
    )
    val surface by transition.animateColor(
        label = "color_surface",
        targetValueByState = { it.surface.getColor(context) },
        transitionSpec = animationSpec,
    )
    val surfaceVariant by transition.animateColor(
        label = "color_surfaceVariant",
        targetValueByState = { it.surfaceVariant.getColor(context) },
        transitionSpec = animationSpec,
    )
    val error by transition.animateColor(
        label = "color_error",
        targetValueByState = { it.error.getColor(context) },
        transitionSpec = animationSpec,
    )
    val errorContainer by transition.animateColor(
        label = "color_errorContainer",
        targetValueByState = { it.errorContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onPrimary by transition.animateColor(
        label = "color_onPrimary",
        targetValueByState = { it.onPrimary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onPrimaryContainer by transition.animateColor(
        label = "color_onPrimaryContainer",
        targetValueByState = { it.onPrimaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onSecondary by transition.animateColor(
        label = "color_onSecondary",
        targetValueByState = { it.onSecondary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onSecondaryContainer by transition.animateColor(
        label = "color_onSecondaryContainer",
        targetValueByState = { it.onSecondaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onTertiary by transition.animateColor(
        label = "color_onTertiary",
        targetValueByState = { it.onTertiary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onTertiaryContainer by transition.animateColor(
        label = "color_onTertiaryContainer",
        targetValueByState = { it.onTertiaryContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onBackground by transition.animateColor(
        label = "color_onBackground",
        targetValueByState = { it.onBackground.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onSurface by transition.animateColor(
        label = "color_onSurface",
        targetValueByState = { it.onSurface.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onSurfaceVariant by transition.animateColor(
        label = "color_onSurfaceVariant",
        targetValueByState = { it.onSurfaceVariant.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onError by transition.animateColor(
        label = "color_onError",
        targetValueByState = { it.onError.getColor(context) },
        transitionSpec = animationSpec,
    )
    val onErrorContainer by transition.animateColor(
        label = "color_onErrorContainer",
        targetValueByState = { it.onErrorContainer.getColor(context) },
        transitionSpec = animationSpec,
    )
    val inversePrimary by transition.animateColor(
        label = "color_inversePrimary",
        targetValueByState = { it.inversePrimary.getColor(context) },
        transitionSpec = animationSpec,
    )
    val inverseSurface by transition.animateColor(
        label = "color_inverseSurface",
        targetValueByState = { it.inverseSurface.getColor(context) },
        transitionSpec = animationSpec,
    )
    val inverseOnSurface by transition.animateColor(
        label = "color_inverseOnSurface",
        targetValueByState = { it.inverseOnSurface.getColor(context) },
        transitionSpec = animationSpec,
    )
    val outline by transition.animateColor(
        label = "color_outline",
        targetValueByState = { it.outline.getColor(context) },
        transitionSpec = animationSpec,
    )

    return colorProviders(
        primary = primary.toColorProvider(),
        onPrimary = onPrimary.toColorProvider(),
        primaryContainer = primaryContainer.toColorProvider(),
        onPrimaryContainer = onPrimaryContainer.toColorProvider(),
        inversePrimary = inversePrimary.toColorProvider(),
        secondary = secondary.toColorProvider(),
        onSecondary = onSecondary.toColorProvider(),
        secondaryContainer = secondaryContainer.toColorProvider(),
        onSecondaryContainer = onSecondaryContainer.toColorProvider(),
        tertiary = tertiary.toColorProvider(),
        onTertiary = onTertiary.toColorProvider(),
        tertiaryContainer = tertiaryContainer.toColorProvider(),
        onTertiaryContainer = onTertiaryContainer.toColorProvider(),
        background = background.toColorProvider(),
        onBackground = onBackground.toColorProvider(),
        surface = surface.toColorProvider(),
        onSurface = onSurface.toColorProvider(),
        surfaceVariant = surfaceVariant.toColorProvider(),
        onSurfaceVariant = onSurfaceVariant.toColorProvider(),
        inverseSurface = inverseSurface.toColorProvider(),
        inverseOnSurface = inverseOnSurface.toColorProvider(),
        error = error.toColorProvider(),
        onError = onError.toColorProvider(),
        errorContainer = errorContainer.toColorProvider(),
        onErrorContainer = onErrorContainer.toColorProvider(),
        outline = outline.toColorProvider(),
    )
}

fun getColorProviders(context: Context, baseColor: Color): ColorProviders {
    val isDarkTheme = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val scheme = baseColor.toDynamicScheme(
        isDark = isDarkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.TonalSpot
    )

    return colorProviders(
        primary = Color(scheme.primary).toColorProvider(),
        onPrimary = Color(scheme.onPrimary).toColorProvider(),
        primaryContainer = Color(scheme.primaryContainer).toColorProvider(),
        onPrimaryContainer = Color(scheme.onPrimaryContainer).toColorProvider(),
        secondary = Color(scheme.secondary).toColorProvider(),
        onSecondary = Color(scheme.onSecondary).toColorProvider(),
        secondaryContainer = Color(scheme.secondaryContainer).toColorProvider(),
        onSecondaryContainer = Color(scheme.onSecondaryContainer).toColorProvider(),
        tertiary = Color(scheme.tertiary).toColorProvider(),
        onTertiary = Color(scheme.onTertiary).toColorProvider(),
        tertiaryContainer = Color(scheme.tertiaryContainer).toColorProvider(),
        onTertiaryContainer = Color(scheme.onTertiaryContainer).toColorProvider(),
        error = Color(scheme.error).toColorProvider(),
        errorContainer = Color(scheme.errorContainer).toColorProvider(),
        onError = Color(scheme.onError).toColorProvider(),
        onErrorContainer = Color(scheme.onErrorContainer).toColorProvider(),
        background = Color(scheme.background).toColorProvider(),
        onBackground = Color(scheme.onBackground).toColorProvider(),
        surface = Color(scheme.surface).toColorProvider(),
        onSurface = Color(scheme.onSurface).toColorProvider(),
        surfaceVariant = Color(scheme.surfaceVariant).toColorProvider(),
        onSurfaceVariant = Color(scheme.onSurfaceVariant).toColorProvider(),
        outline = Color(scheme.outline).toColorProvider(),
        inverseOnSurface = Color(scheme.inverseOnSurface).toColorProvider(),
        inverseSurface = Color(scheme.inverseSurface).toColorProvider(),
        inversePrimary = Color(scheme.inversePrimary).toColorProvider(),
    )
}

@SuppressLint("RestrictedApi")
fun Color.toColorProvider() = ColorProvider(this)
