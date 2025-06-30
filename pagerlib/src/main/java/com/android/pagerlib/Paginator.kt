package com.android.pagerlib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Core pagination controller that manages loading, caching, and state management
 * for paginated data in an MVI-friendly way.
 */
class Paginator<T>(
    private val repository: PaginationRepository<T>,
    private val pageSize: Int = 20,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val cache = PaginationCache<T>()
    
    // MVI State Management
    private val _state = MutableStateFlow(
        PaginationState<T>(pageSize = pageSize)
    )
    val state: StateFlow<PaginationState<T>> = _state.asStateFlow()
    
    // Side Effects Channel
    private val _sideEffects = Channel<PaginationSideEffect>(Channel.BUFFERED)
    val sideEffects: Flow<PaginationSideEffect> = _sideEffects.receiveAsFlow()
    
    // Track ongoing operations
    private var isLoadingPage = false
    
    /**
     * Handles pagination events.
     */
    fun handleEvent(event: PaginationEvent) {
        when (event) {
            is PaginationEvent.LoadFirstPage -> loadFirstPage()
            is PaginationEvent.LoadNextPage -> loadNextPage()
            is PaginationEvent.Refresh -> refresh()
            is PaginationEvent.Retry -> retry()
            is PaginationEvent.Clear -> clear()
        }
    }
    
    /**
     * Loads the first page of data.
     */
    private fun loadFirstPage() {
        if (isLoadingPage) return
        
        scope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                loadPage(0)
            } catch (e: Exception) {
                handleError(e, "Failed to load first page")
            }
        }
    }
    
    /**
     * Loads the next page of data.
     */
    private fun loadNextPage() {
        val currentState = _state.value
        if (isLoadingPage || !currentState.hasNextPage || currentState.isLoadingMore) return
        
        scope.launch {
            try {
                updateState { copy(isLoadingMore = true, error = null) }
                loadPage(currentState.currentPage + 1)
            } catch (e: Exception) {
                handleError(e, "Failed to load next page")
            }
        }
    }
    
    /**
     * Refreshes the pagination by clearing cache and reloading from first page.
     */
    private fun refresh() {
        if (isLoadingPage) return
        
        scope.launch {
            try {
                cache.clear()
                updateState { 
                    copy(
                        items = emptyList(),
                        currentPage = 0,
                        hasNextPage = true,
                        isLoading = true,
                        error = null
                    ) 
                }
                loadPage(0)
                emitSideEffect(PaginationSideEffect.RefreshCompleted)
            } catch (e: Exception) {
                handleError(e, "Failed to refresh")
            }
        }
    }
    
    /**
     * Retries the last failed operation.
     */
    private fun retry() {
        val currentState = _state.value
        if (currentState.error != null) {
            if (currentState.items.isEmpty()) {
                loadFirstPage()
            } else {
                loadNextPage()
            }
        }
    }
    
    /**
     * Clears all data and resets pagination state.
     */
    private fun clear() {
        cache.clear()
        updateState { 
            PaginationState(pageSize = pageSize)
        }
    }
    
    /**
     * Loads a specific page from repository or cache.
     */
    private suspend fun loadPage(page: Int) = withContext(Dispatchers.IO) {
        isLoadingPage = true
        
        try {
            // Check cache first
            if (cache.hasPage(page)) {
                val cachedItems = cache.getAllItems()
                withContext(Dispatchers.Main) {
                    updateState { 
                        copy(
                            items = cachedItems,
                            currentPage = page,
                            isLoading = false,
                            isLoadingMore = false
                        ) 
                    }
                }
                return@withContext
            }
            
            // Load from repository
            val result = repository.loadPage(pageSize, page)
            
            withContext(Dispatchers.Main) {
                when (result) {
                    is PaginationResult.Success -> {
                        // Store in cache
                        cache.storePage(page, result.items)
                        cache.setTotalCount(result.totalCount)
                        
                        val allItems = cache.getAllItems()
                        
                        updateState { 
                            copy(
                                items = allItems,
                                currentPage = result.currentPage,
                                hasNextPage = result.hasNextPage,
                                totalCount = result.totalCount,
                                isLoading = false,
                                isLoadingMore = false,
                                error = null
                            ) 
                        }
                        
                        if (!result.hasNextPage) {
                            emitSideEffect(PaginationSideEffect.EndReached)
                        }
                    }
                    
                    is PaginationResult.Error -> {
                        handleError(result.throwable, result.message)
                    }
                    
                    is PaginationResult.Loading -> {
                        // This shouldn't happen from repository, but handle gracefully
                        updateState { 
                            copy(
                                isLoading = page == 0,
                                isLoadingMore = page > 0
                            ) 
                        }
                    }
                }
            }
        } finally {
            isLoadingPage = false
        }
    }
    
    /**
     * Handles errors by updating state and emitting side effects.
     */
    private suspend fun handleError(throwable: Throwable, defaultMessage: String) {
        val errorMessage = throwable.message ?: defaultMessage
        updateState { 
            copy(
                isLoading = false,
                isLoadingMore = false,
                error = errorMessage
            ) 
        }
        emitSideEffect(PaginationSideEffect.ShowError(errorMessage))
    }
    
    /**
     * Updates the current state.
     */
    private fun updateState(update: PaginationState<T>.() -> PaginationState<T>) {
        _state.value = _state.value.update()
    }
    
    /**
     * Emits a side effect.
     */
    private suspend fun emitSideEffect(effect: PaginationSideEffect) {
        _sideEffects.send(effect)
    }
    
    /**
     * Gets the current cached item count.
     */
    fun getCachedItemCount(): Int = cache.getCachedItemCount()
    
    /**
     * Checks if a specific page is cached.
     */
    fun isPageCached(page: Int): Boolean = cache.hasPage(page)
    
    /**
     * Gets items for a specific page range.
     */
    fun getItemsInRange(startIndex: Int, endIndex: Int): List<T> {
        val allItems = cache.getAllItems()
        return if (startIndex < allItems.size && endIndex <= allItems.size) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
}