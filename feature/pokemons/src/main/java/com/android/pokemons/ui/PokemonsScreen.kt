package com.android.pokemons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.model.PokemonDomain

@Composable
fun PokemonsScreen() {
    val viewModel = hiltViewModel<PokemonsViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetPokemons(20, 0))
    }

    if (state.isLoading)
        LoadingIndicator()
    else
        Column(modifier = Modifier.fillMaxSize()) {
            CenterFocusLazyColumn(state.pokemons ?: emptyList()) {
                viewModel.setEvent(Event.HandleLoading)
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
    modifier: Modifier = Modifier,
    onImgLoaded: () -> Unit = {}
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
        modifier = modifier
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

            PokemonCardPagerStyle(
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
                isFocused =index == centerIndex ,
                onImgLoaded = onImgLoaded
            )
        }
    }
}



@Composable
fun PokemonCardPagerStyle(
    pokemon: PokemonDomain,
    modifier: Modifier = Modifier,
    isFocused: Boolean,
    onImgLoaded: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(if (isFocused) 24.dp else 8.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(Modifier.fillMaxSize()) {
            NetworkImage(
                url = pokemon.url,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(if (isFocused)240.dp else 170.dp),
                onSuccess = { onImgLoaded() }
            )
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Text(
                    pokemon.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
        }
    }
}




@Preview
@Composable
fun ScreenshotStyleVerticalPagerPreview() {
    val pokemons = List(20) { index ->
        PokemonDomain(name = "Pokemon $index", url = "https://example.com/pokemon$index.png")
    }
    CenterFocusLazyColumn(pokemons = pokemons) {}
}


