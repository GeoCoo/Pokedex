package com.android.core_ui.component

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.pokedex.core.core_resources.R
import kotlinx.coroutines.delay

/**
 * Execute an effect based on [Lifecycle.Event]
 *
 *  @param lifecycleOwner The lifecycle owner
 *  @param lifecycleEvent The lifecycle event that code needs to be executed
 */
@Composable
fun LifecycleEffect(
    lifecycleOwner: LifecycleOwner, lifecycleEvent: Lifecycle.Event, block: () -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                block()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    onSuccess: (() -> Unit)
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        modifier = modifier,
        contentScale = contentScale,
        onSuccess = { onSuccess() }
    )
}

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds,
    onColorExtracted: (Color) -> Unit) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .allowHardware(false) // Needed for Palette
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        modifier = modifier,
        contentScale = contentScale,
        onSuccess = { success ->
            val bitmap = (success.result.drawable as? BitmapDrawable)?.bitmap ?: return@AsyncImage
            Palette.from(bitmap).generate { palette ->
                val colorInt = palette?.getDominantColor(Color.Gray.toArgb()) ?: Color.Gray.toArgb()
                onColorExtracted(Color(colorInt))
            }
        }
    )
}

@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = if (expanded) stringResource(R.string.show_less_label) else stringResource(R.string.show_more_label),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable { expanded = !expanded }
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}


@Composable
fun CircleWithText(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun PercentageProgressCircle(
    percent: Float?,
    size: Int = 28,
    strokeWidth: Float = 6f,
    backgroundColor: Color = MaterialTheme.colorScheme.outline,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * ((percent ?: 0f) / 10f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$percent",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChanged(it) },
        placeholder = {
            Text(
                stringResource(R.string.search_placeholder),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp, shape = shape, clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface, shape = shape
            )
    )
}

@Composable
fun DebouncedSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    debounceMillis: Long = 300
) {
    var text by remember { mutableStateOf(query) }

    LaunchedEffect(text) {
        delay(debounceMillis)
        onQueryChanged(text)
    }

    SearchBar(
        query = text,
        onQueryChanged = { text = it }
    )
}

@Composable
fun FadingImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    fade: Boolean = true // controls looping
) {
    // Animate alpha between 0.3 and 1f infinitely while fade is true
    val infiniteTransition = rememberInfiniteTransition(label = "fade")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fadeAlpha"
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.alpha(if (fade) alpha else 1f)
    )
}

@Composable
fun GlassyBadge(
    text: String,
    icon: String? = null,
    modifier: Modifier = Modifier
) {
    // Use glassy white gradient + blur for "glass" look
    Box(
        modifier = modifier
            .graphicsLayer { alpha = 0.90f } // For smooth glass feel
            .blur(1.dp) // Blur behind badge (API 31+)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.2.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Text(
                    icon,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(end = 7.dp)
                )
            }
            Text(
                text,
                color = Color.Black,
                fontSize = 16.sp,
            )
        }
    }
}
