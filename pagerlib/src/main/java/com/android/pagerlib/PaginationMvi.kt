package com.android.pagerlib

import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState

/**
 * MVI State for pagination.
 */
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true,
    val error: String? = null,
    val totalCount: Int? = null,
    val pageSize: Int = 20
) : ViewState

/**
 * MVI Events for pagination.
 */
sealed class PaginationEvent : ViewEvent {
    /**
     * Load the first page of items.
     */
    object LoadFirstPage : PaginationEvent()
    
    /**
     * Load the next page of items.
     */
    object LoadNextPage : PaginationEvent()
    
    /**
     * Refresh the entire pagination by reloading from the first page.
     */
    object Refresh : PaginationEvent()
    
    /**
     * Retry loading after an error.
     */
    object Retry : PaginationEvent()
    
    /**
     * Clear all items and reset pagination state.
     */
    object Clear : PaginationEvent()
}

/**
 * MVI Side Effects for pagination.
 */
sealed class PaginationSideEffect : ViewSideEffect {
    /**
     * Emitted when an error occurs during pagination.
     */
    data class ShowError(val message: String) : PaginationSideEffect()
    
    /**
     * Emitted when refresh is completed.
     */
    object RefreshCompleted : PaginationSideEffect()
    
    /**
     * Emitted when all available data has been loaded.
     */
    object EndReached : PaginationSideEffect()
}