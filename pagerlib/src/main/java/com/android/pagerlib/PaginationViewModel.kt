package com.android.pagerlib

import androidx.lifecycle.viewModelScope
import com.android.core_ui.base.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * MVI ViewModel that integrates Paginator with the existing MVI architecture.
 * This provides a bridge between the pagination library and MVI ViewModels.
 */
abstract class PaginationViewModel<T>(
    private val paginator: Paginator<T>
) : MviViewModel<PaginationEvent, PaginationState<T>, PaginationSideEffect>() {
    
    init {
        // Subscribe to paginator state changes
        paginator.state
            .onEach { paginationState ->
                setState { paginationState }
            }
            .launchIn(viewModelScope)
        
        // Subscribe to paginator side effects
        paginator.sideEffects
            .onEach { sideEffect ->
                setEffect { sideEffect }
            }
            .launchIn(viewModelScope)
    }
    
    override fun setInitialState(): PaginationState<T> = PaginationState()
    
    override fun handleEvents(event: PaginationEvent) {
        paginator.handleEvent(event)
    }
    
    /**
     * Convenience method to load first page.
     */
    fun loadFirstPage() {
        setEvent(PaginationEvent.LoadFirstPage)
    }
    
    /**
     * Convenience method to load next page.
     */
    fun loadNextPage() {
        setEvent(PaginationEvent.LoadNextPage)
    }
    
    /**
     * Convenience method to refresh.
     */
    fun refresh() {
        setEvent(PaginationEvent.Refresh)
    }
    
    /**
     * Convenience method to retry.
     */
    fun retry() {
        setEvent(PaginationEvent.Retry)
    }
    
    /**
     * Gets the current cached item count.
     */
    fun getCachedItemCount(): Int = paginator.getCachedItemCount()
    
    /**
     * Checks if should load more items based on current position.
     */
    fun shouldLoadMore(currentIndex: Int, threshold: Int = 5): Boolean {
        val state = viewState.value
        return !state.isLoadingMore && 
               state.hasNextPage && 
               (currentIndex >= state.items.size - threshold)
    }
}