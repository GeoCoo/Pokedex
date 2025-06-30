package com.android.pagerlib.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.pagerlib.PaginationState

/**
 * Composable that provides pagination functionality with LazyColumn.
 * Automatically triggers loading more items when approaching the end of the list.
 */
@Composable
fun <T> PaginatedLazyColumn(
    state: PaginationState<T>,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    loadMoreThreshold: Int = 5,
    itemContent: @Composable (T) -> Unit,
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    errorContent: @Composable (String, () -> Unit) -> Unit = { error, retry ->
        DefaultErrorContent(error, retry)
    },
    emptyContent: @Composable () -> Unit = { DefaultEmptyContent() },
    loadingMoreContent: @Composable () -> Unit = { DefaultLoadingMoreContent() }
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            lastVisibleItemIndex > (totalItemsNumber - loadMoreThreshold)
        }
    }
    
    // Trigger load more when needed
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state.hasNextPage && !state.isLoadingMore) {
            onLoadMore()
        }
    }
    
    Column(modifier = modifier) {
        when {
            // Initial loading state
            state.isLoading && state.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    loadingContent()
                }
            }
            
            // Error state with no items
            state.error != null && state.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    errorContent(state.error, onRetry)
                }
            }
            
            // Empty state
            !state.isLoading && state.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    emptyContent()
                }
            }
            
            // Content with items
            else -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items) { item ->
                        itemContent(item)
                    }
                    
                    // Loading more indicator
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                loadingMoreContent()
                            }
                        }
                    }
                    
                    // Error state while having items
                    if (state.error != null && state.items.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                errorContent(state.error, onRetry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultLoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DefaultLoadingMoreContent() {
    CircularProgressIndicator()
}

@Composable
private fun DefaultErrorContent(error: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun DefaultEmptyContent() {
    Text(
        text = "No items found",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}