package com.android.pagerlib

/**
 * In-memory cache for paginated items.
 * Provides efficient storage and retrieval of paginated data.
 */
class PaginationCache<T> {
    private val pageCache = mutableMapOf<Int, List<T>>()
    private val allItems = mutableListOf<T>()
    private var totalCount: Int? = null
    
    /**
     * Stores items for a specific page.
     */
    fun storePage(page: Int, items: List<T>) {
        pageCache[page] = items
        rebuildAllItems()
    }
    
    /**
     * Retrieves items for a specific page.
     */
    fun getPage(page: Int): List<T>? = pageCache[page]
    
    /**
     * Checks if a page is cached.
     */
    fun hasPage(page: Int): Boolean = pageCache.containsKey(page)
    
    /**
     * Gets all cached items in order.
     */
    fun getAllItems(): List<T> = allItems.toList()
    
    /**
     * Gets the number of cached pages.
     */
    fun getCachedPageCount(): Int = pageCache.size
    
    /**
     * Gets the total number of cached items.
     */
    fun getCachedItemCount(): Int = allItems.size
    
    /**
     * Sets the total count of items available from the data source.
     */
    fun setTotalCount(count: Int?) {
        totalCount = count
    }
    
    /**
     * Gets the total count of items.
     */
    fun getTotalCount(): Int? = totalCount
    
    /**
     * Clears all cached data.
     */
    fun clear() {
        pageCache.clear()
        allItems.clear()
        totalCount = null
    }
    
    /**
     * Removes items from cache starting from a specific page.
     * Useful for refresh operations.
     */
    fun clearFromPage(page: Int) {
        val keysToRemove = pageCache.keys.filter { it >= page }
        keysToRemove.forEach { pageCache.remove(it) }
        rebuildAllItems()
    }
    
    /**
     * Rebuilds the allItems list from cached pages in order.
     */
    private fun rebuildAllItems() {
        allItems.clear()
        val sortedPages = pageCache.keys.sorted()
        sortedPages.forEach { page ->
            pageCache[page]?.let { items ->
                allItems.addAll(items)
            }
        }
    }
    
    /**
     * Gets the highest cached page number.
     */
    fun getHighestCachedPage(): Int? = pageCache.keys.maxOrNull()
    
    /**
     * Checks if the cache is empty.
     */
    fun isEmpty(): Boolean = pageCache.isEmpty()
}