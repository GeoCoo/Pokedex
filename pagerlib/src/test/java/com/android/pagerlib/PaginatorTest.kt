package com.android.pagerlib

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaginatorTest {
    
    private lateinit var testScope: TestScope
    private lateinit var mockRepository: TestPaginationRepository
    private lateinit var paginator: Paginator<String>
    
    @Before
    fun setup() {
        testScope = TestScope()
        mockRepository = TestPaginationRepository()
        paginator = Paginator(mockRepository, pageSize = 10, scope = testScope)
    }
    
    @Test
    fun `initial state should be empty`() = testScope.runTest {
        val state = paginator.state.value
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(0, state.currentPage)
        assertTrue(state.hasNextPage)
    }
    
    @Test
    fun `load first page should update state correctly`() = testScope.runTest {
        // Setup mock response
        mockRepository.setupPage(0, listOf("item1", "item2", "item3"))
        
        // Load first page
        paginator.handleEvent(PaginationEvent.LoadFirstPage)
        
        // Wait for state update
        testScope.testScheduler.advanceUntilIdle()
        
        val state = paginator.state.value
        assertEquals(listOf("item1", "item2", "item3"), state.items)
        assertFalse(state.isLoading)
        assertEquals(0, state.currentPage)
    }
    
    @Test
    fun `load next page should append items`() = testScope.runTest {
        // Setup first page
        mockRepository.setupPage(0, listOf("item1", "item2"))
        mockRepository.setupPage(1, listOf("item3", "item4"))
        
        // Load first page
        paginator.handleEvent(PaginationEvent.LoadFirstPage)
        testScope.testScheduler.advanceUntilIdle()
        
        // Load next page
        paginator.handleEvent(PaginationEvent.LoadNextPage)
        testScope.testScheduler.advanceUntilIdle()
        
        val state = paginator.state.value
        assertEquals(listOf("item1", "item2", "item3", "item4"), state.items)
        assertEquals(1, state.currentPage)
    }
    
    @Test
    fun `cache should store and retrieve pages correctly`() {
        val cache = PaginationCache<String>()
        
        // Store pages
        cache.storePage(0, listOf("item1", "item2"))
        cache.storePage(1, listOf("item3", "item4"))
        
        // Verify storage
        assertTrue(cache.hasPage(0))
        assertTrue(cache.hasPage(1))
        assertFalse(cache.hasPage(2))
        
        // Verify retrieval
        assertEquals(listOf("item1", "item2"), cache.getPage(0))
        assertEquals(listOf("item3", "item4"), cache.getPage(1))
        assertEquals(listOf("item1", "item2", "item3", "item4"), cache.getAllItems())
    }
    
    @Test
    fun `error handling should update state correctly`() = testScope.runTest {
        // Setup mock to throw error
        mockRepository.setupError("Test error")
        
        // Try to load first page
        paginator.handleEvent(PaginationEvent.LoadFirstPage)
        testScope.testScheduler.advanceUntilIdle()
        
        val state = paginator.state.value
        assertEquals("Test error", state.error)
        assertFalse(state.isLoading)
        assertTrue(state.items.isEmpty())
    }
    
    // Test utility class
    private class TestPaginationRepository : PaginationRepository<String> {
        private val pages = mutableMapOf<Int, List<String>>()
        private var shouldError = false
        private var errorMessage = ""
        
        fun setupPage(page: Int, items: List<String>) {
            pages[page] = items
            shouldError = false
        }
        
        fun setupError(message: String) {
            shouldError = true
            errorMessage = message
        }
        
        override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<String> {
            if (shouldError) {
                return PaginationResult.Error(Exception(errorMessage))
            }
            
            val items = pages[page] ?: emptyList()
            return PaginationResult.Success(
                items = items,
                currentPage = page,
                hasNextPage = items.size == pageSize
            )
        }
    }
}