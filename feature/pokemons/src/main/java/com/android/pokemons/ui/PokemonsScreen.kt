package com.android.pokemons.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.GlassyBadge
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.model.PokemonDomain
import com.android.pokedex.core.core_resources.R

@Preview
@Composable
fun PokemonScreePreview() {
    PokemonsScreen()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonsScreen() {
    val viewModel = hiltViewModel<PokemonsViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value
    val context = LocalContext.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetPokemons(20, 0))
    }

    Scaffold {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_name))
            }
        )
        if (state.isLoading)
            LoadingIndicator()
        else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_pokeball),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(-35f)
                        .offset(x = 125.dp),
                    alignment = Alignment.Center
                )
                CenterFocusLazyColumn(state.pokemons ?: emptyList())
            }
    }


    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is Effect.SampleEffect -> {

                    }
                }
            }
    }
}

@Composable
fun CenterFocusLazyColumn(
    pokemons: List<PokemonDomain>,
) {
    val visibleCount = 3
    var listHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val listState = rememberLazyListState()

    // Fallback: ~180.dp (in px)
    val fallbackCardHeightPx = with(density) { 180.dp.roundToPx() }

    // Use measured height if available, else fallback
    val cardHeightPx = if (listHeightPx > 0) listHeightPx / visibleCount else fallbackCardHeightPx
    val cardHeight = with(density) { cardHeightPx.toDp() }
    val pxPerItem = cardHeightPx

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = with(density) { (cardHeightPx / 2).toDp() }),
        verticalArrangement = Arrangement.spacedBy(
            with(density) { -(cardHeightPx / 2.1f).toDp() }
        ),
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                if (coordinates.size.height > 0 && coordinates.size.height != listHeightPx) {
                    listHeightPx = coordinates.size.height
                }
            }
    ) {
        itemsIndexed(pokemons) { index, pokemon ->
            val first = listState.firstVisibleItemIndex
            val offsetPx = listState.firstVisibleItemScrollOffset
            val centerIndex = first + visibleCount / 2
            val offset = (index - centerIndex) - (offsetPx / pxPerItem.toFloat())

            val angle = when {
                index == centerIndex -> 0f
                index < centerIndex -> -10f
                else -> 10f
            }
            val translationY = offset * (cardHeight / 2.1f)
            val scale = 1f - 0.07f * kotlin.math.abs(offset)

            FloatingImageCard(
                pokemon = pokemon,
                modifier = Modifier
                    .graphicsLayer(
                        rotationZ = angle,
                        translationY = with(density) { translationY.toPx() },
                        scaleX = scale,
                        scaleY = scale
                    )
                    .height(cardHeight)
                    .width(290.dp)
                    .padding(start = 24.dp),
            )
        }
    }
}

@Composable
fun FloatingImageCard(
    pokemon: PokemonDomain,
    modifier: Modifier = Modifier,
) {
    var bgColor by remember { mutableStateOf(Color(0xFFE0E0E0)) }

    Box(
        modifier = modifier
            .width(200.dp)
            .wrapContentHeight(),
    ) {
        // Card background
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxSize(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                GlassyBadge(text = pokemon.name)
            }
        }

        NetworkImage(
            url = pokemon.url,
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-48).dp),
            onColorExtracted = { color ->
                bgColor = color
            })
    }
}

//@Preview
//@Composable
//fun ScreenshotStyleVerticalPagerPreview() {
//    val pokemons = List(20) { index ->
//        PokemonDomain(name = "Pokemon $index", url = "https://example.com/pokemon$index.png")
//    }
//    CenterFocusLazyColumn(pokemons = pokemons) {}
//}




