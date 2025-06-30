package com.android.pagerlib.integration

import com.android.pagerlib.PaginationRepository
import com.android.pagerlib.PaginationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Utility class to help integrate existing repositories with the pagination library.
 */
object PaginationIntegration {
    
    /**
     * Creates a PaginationRepository adapter for repositories that return Flow-based responses.
     * This is useful for adapting existing repositories to work with the pagination library.
     * 
     * @param loadPage A suspend function that loads a page and returns a Flow of the response
     * @param mapToResult A function that maps the response to PaginationResult
     */
    inline fun <T, R> createFlowAdapter(
        crossinline loadPage: suspend (pageSize: Int, page: Int) -> Flow<R>,
        crossinline mapToResult: (R, page: Int) -> PaginationResult<T>
    ): PaginationRepository<T> {
        return object : PaginationRepository<T> {
            override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<T> {
                return try {
                    val response = loadPage(pageSize, page).first()
                    mapToResult(response, page)
                } catch (e: Exception) {
                    PaginationResult.Error(e)
                }
            }
        }
    }
    
    /**
     * Creates a PaginationRepository adapter for simple suspend functions.
     * 
     * @param loadPage A suspend function that loads a page and returns the result
     */
    inline fun <T> createSuspendAdapter(
        crossinline loadPage: suspend (pageSize: Int, page: Int) -> PaginationResult<T>
    ): PaginationRepository<T> {
        return object : PaginationRepository<T> {
            override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<T> {
                return try {
                    loadPage(pageSize, page)
                } catch (e: Exception) {
                    PaginationResult.Error(e)
                }
            }
        }
    }
    
    /**
     * Creates a PaginationRepository for offset-based pagination.
     * This is useful when the API uses offset/limit instead of page numbers.
     * 
     * @param loadData A suspend function that loads data using offset and limit
     * @param mapToItems A function that extracts items from the response
     * @param hasNextPage A function that determines if there are more pages
     * @param getTotalCount An optional function that gets the total count
     */
    inline fun <T, R> createOffsetAdapter(
        crossinline loadData: suspend (limit: Int, offset: Int) -> R,
        crossinline mapToItems: (R) -> List<T>,
        crossinline hasNextPage: (R, currentPage: Int, pageSize: Int) -> Boolean,
        crossinline getTotalCount: (R) -> Int? = { null }
    ): PaginationRepository<T> {
        return object : PaginationRepository<T> {
            override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<T> {
                return try {
                    val offset = page * pageSize
                    val response = loadData(pageSize, offset)
                    val items = mapToItems(response)
                    val hasNext = hasNextPage(response, page, pageSize)
                    val totalCount = getTotalCount(response)
                    
                    PaginationResult.Success(
                        items = items,
                        currentPage = page,
                        hasNextPage = hasNext,
                        totalCount = totalCount
                    )
                } catch (e: Exception) {
                    PaginationResult.Error(e)
                }
            }
        }
    }
}