package com.android.pagerlib

/**
 * Generic interface for repositories that support pagination.
 * T represents the type of items being paginated.
 */
interface PaginationRepository<T> {
    /**
     * Fetches a page of items from the data source.
     * 
     * @param pageSize The number of items to fetch per page
     * @param page The page number (0-based indexing)
     * @return PaginationResult containing the fetched items and pagination info
     */
    suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<T>
}

/**
 * Result of a pagination request.
 */
sealed class PaginationResult<T> {
    data class Success<T>(
        val items: List<T>,
        val currentPage: Int,
        val hasNextPage: Boolean,
        val totalCount: Int? = null
    ) : PaginationResult<T>()
    
    data class Error<T>(
        val throwable: Throwable,
        val message: String = throwable.message ?: "Unknown error"
    ) : PaginationResult<T>()
    
    data class Loading<T>(val page: Int) : PaginationResult<T>()
}