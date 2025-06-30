package com.android.pokemons.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.GlassyBadge
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.model.PokemonDomain
import com.android.pokedex.core.core_resources.R
import kotlin.math.abs


@Preview
@Composable
fun PokemonScreePreview() {
    val pokemons = List(20) { index ->
        PokemonDomain(name = "Pokemon $index", url = "https://example.com/pokemon$index.png")
    }
    VerticalPagerWith3VisibleItems(pokemons, {})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonsScreen(onPokemonClick: (String) -> Unit) {
    val viewModel = hiltViewModel<PokemonsViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetPokemons(20, state.page, state.pokemons))
    }

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
            VerticalPagerWith3VisibleItems(
                state.pokemons ?: emptyList(),
                nextPokemons = {
                    viewModel.setEvent(Event.GetPokemons(20, state.page, state.pokemons))
                },
                onClick = {
                    onPokemonClick(it)
                })

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalPagerWith3VisibleItems(
    pokemons: List<PokemonDomain>,
    nextPokemons: () -> Unit = {},
    onClick: (String) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { pokemons.size })
    val density = LocalDensity.current

    var screenHeightPx by remember { mutableIntStateOf(0) }
    val visibleCount = 3
    val fallbackCardHeightPx = with(density) { 180.dp.roundToPx() }
    val cardHeightPx =
        if (screenHeightPx > 0) screenHeightPx / visibleCount else fallbackCardHeightPx
    val cardHeight = with(density) { cardHeightPx.toDp() }

    VerticalPager(
        state = pagerState,
        contentPadding = PaddingValues(vertical = with(density) { (cardHeightPx / 4).toDp() }),
        pageSpacing = with(density) { -(cardHeightPx / 5).toDp() },
        pageSize = PageSize.Fixed(cardHeight),
        beyondBoundsPageCount = 3,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenHeightPx = coordinates.size.height
            }
    ) { page ->
        val firstVisiblePage = if (pagerState.currentPageOffsetFraction >= 0f) {
            pagerState.currentPage
        } else {
            pagerState.currentPage - 1
        }
        val centerIndex = firstVisiblePage + visibleCount / 2
        val offset = (page - centerIndex) - pagerState.currentPageOffsetFraction

        val angle = when {
            page == centerIndex -> 0f
            page < centerIndex -> -10f
            else -> 10f
        }

        val translateY = offset * (cardHeight.value / 2.1f)
        val scale = 1f - 0.07f * abs(offset)
        val zIndex = if (page == centerIndex) 1f else 0f

        FloatingImageCard(
            pokemon = pokemons[page],
            modifier = Modifier
                .zIndex(zIndex)
                .graphicsLayer(
                    rotationZ = angle,
                    translationY = with(density) { translateY },
                    scaleX = scale,
                    scaleY = scale,
                )
                .height(cardHeight)
                .width(290.dp),
            onClick = {
                onClick(it)
            }
        )

        LaunchedEffect(page == pokemons.size - 3) {
            nextPokemons()
        }
    }
}

@Composable
fun FloatingImageCard(
    pokemon: PokemonDomain,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    var bgColor by remember { mutableStateOf(Color(0xFFE0E0E0)) }

    Box(
        modifier = modifier
            .width(50.dp)
            .wrapContentHeight(),
    ) {
        // Card background
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxSize(),
            onClick = { onClick(pokemon.name) },
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




