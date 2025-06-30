package com.android.pokemons.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.model.PokemonDomain
import com.android.pagerlib.PaginationSideEffect
import com.android.pokedex.core.core_resources.R
import kotlin.math.abs

/**
 * Alternative Pokemon screen using the pagination library.
 * This demonstrates how to integrate PagerLib with the existing UI components.
 * 
 * Key differences from the original:
 * - Uses PaginationViewModel for state management
 * - Automatic pagination handling
 * - Better error handling and loading states
 * - Cached data for better performance
 */
@Composable
fun PokemonsScreenWithPager(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonsViewModelWithPager = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.viewState.value
    
    // Initialize pagination on screen creation
    LifecycleEffect(
        lifecycleOwner = lifecycleOwner,
        lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.loadPokemons()
    }
    
    // Handle pagination side effects
    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is PaginationSideEffect.ShowError -> {
                        // Handle error display - could show snackbar or toast
                        // For now, the error is handled in the UI state
                    }
                    is PaginationSideEffect.RefreshCompleted -> {
                        // Handle refresh completion if needed
                    }
                    is PaginationSideEffect.EndReached -> {
                        // Handle when all Pokemon are loaded
                    }
                }
            }
    }
    
    // Show loading indicator during initial load
    if (state.isLoading && state.items.isEmpty()) {
        LoadingIndicator()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Background image
            Image(
                painter = painterResource(R.drawable.bg_pokeball),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-35f)
                    .offset(x = 125.dp),
                alignment = Alignment.Center
            )
            
            // Pokemon list with pagination
            VerticalPagerWithPagination(
                pokemons = state.items,
                isLoadingMore = state.isLoadingMore,
                hasNextPage = state.hasNextPage,
                onLoadMore = { viewModel.loadNextPage() },
                onPaginationCheck = { index -> viewModel.checkPaginationNeeds(index) },
                onClick = onPokemonClick
            )
        }
    }
}

/**
 * Enhanced version of VerticalPagerWith3VisibleItems that integrates with pagination library.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalPagerWithPagination(
    pokemons: List<PokemonDomain>,
    isLoadingMore: Boolean,
    hasNextPage: Boolean,
    onLoadMore: () -> Unit,
    onPaginationCheck: (Int) -> Unit,
    onClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pokemons.size })
    val density = LocalDensity.current
    
    var screenHeightPx by remember { mutableIntStateOf(0) }
    val visibleCount = 3
    val fallbackCardHeightPx = with(density) { 180.dp.roundToPx() }
    val cardHeightPx = if (screenHeightPx > 0) screenHeightPx / visibleCount else fallbackCardHeightPx
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
            onClick = onClick
        )
        
        // Trigger pagination check when approaching the end
        LaunchedEffect(page) {
            onPaginationCheck(page)
        }
        
        // Show loading indicator for the last visible page when loading more
        if (isLoadingMore && page == pokemons.size - 1) {
            LaunchedEffect(Unit) {
                // Could add a loading indicator overlay here
            }
        }
    }
}