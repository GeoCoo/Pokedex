# PagerLib - MVI-Friendly Pagination Library

A standalone Kotlin library module for pagination that integrates seamlessly with MVI architectures and provides efficient in-memory caching.

## Features

- **MVI-Compatible**: Built with MVI architecture in mind using ViewState, ViewEvent, and ViewSideEffect patterns
- **In-Memory Caching**: Efficiently caches loaded pages to reduce unnecessary network requests
- **Generic & Reusable**: Works with any data type and repository implementation
- **Compose Integration**: Provides ready-to-use Compose components for common UI patterns
- **Flexible Integration**: Easy-to-use adapters for existing repositories
- **Lifecycle Aware**: Integrates with ViewModel scope and lifecycle management

## Core Components

### 1. PaginationRepository<T>
Generic interface for data sources that support pagination.

```kotlin
interface PaginationRepository<T> {
    suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<T>
}
```

### 2. Paginator<T>
Core pagination controller that manages state, caching, and loading logic.

```kotlin
val paginator = Paginator(
    repository = myRepository,
    pageSize = 20
)
```

### 3. PaginationViewModel<T>
MVI ViewModel that bridges the paginator with the existing MVI architecture.

```kotlin
class MyPaginationViewModel(
    repository: PaginationRepository<MyItem>
) : PaginationViewModel<MyItem>(
    paginator = Paginator(repository, pageSize = 20)
)
```

### 4. PaginationState<T>
MVI-compatible state containing pagination data and status.

```kotlin
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true,
    val error: String? = null,
    val totalCount: Int? = null,
    val pageSize: Int = 20
)
```

## Quick Start

### 1. Create a Repository Adapter

For existing repositories that use Flow-based responses:

```kotlin
val paginationRepository = PaginationIntegration.createFlowAdapter(
    loadPage = { pageSize, page ->
        val offset = page * pageSize
        myExistingRepository.getData(pageSize, offset)
    },
    mapToResult = { response, page ->
        when (response) {
            is ApiResponse.Success -> PaginationResult.Success(
                items = response.data,
                currentPage = page,
                hasNextPage = response.data.size == pageSize
            )
            is ApiResponse.Error -> PaginationResult.Error(response.exception)
        }
    }
)
```

### 2. Create a ViewModel

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    repository: MyPaginationRepository
) : PaginationViewModel<MyItem>(
    paginator = Paginator(repository, pageSize = 20)
) {
    
    fun initialize() {
        loadFirstPage()
    }
}
```

### 3. Use in Compose UI

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val state = viewModel.viewState.value
    
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    
    PaginatedLazyColumn(
        state = state,
        onLoadMore = { viewModel.loadNextPage() },
        onRetry = { viewModel.retry() },
        itemContent = { item ->
            MyItemCard(item = item)
        }
    )
}
```

## Integration with Existing Code

### Pokemon Example Integration

The library includes a complete sample integration with the existing Pokemon data layer:

```kotlin
// Repository adapter
class PokemonPaginationRepository @Inject constructor(
    private val pokemonInteractor: PokemonInteractor
) : PaginationRepository<PokemonDomain> {
    
    override suspend fun loadPage(pageSize: Int, page: Int): PaginationResult<PokemonDomain> {
        val offset = page * pageSize
        val response = pokemonInteractor.getPokemons(pageSize, offset).first()
        
        return when (response) {
            is PokemonPartialState.Success -> PaginationResult.Success(
                items = response.pokemons ?: emptyList(),
                currentPage = page,
                hasNextPage = response.pokemons?.size == pageSize
            )
            // Handle other states...
        }
    }
}

// ViewModel
@HiltViewModel
class PokemonPaginationViewModel @Inject constructor(
    pokemonPaginationRepository: PokemonPaginationRepository
) : PaginationViewModel<PokemonDomain>(
    paginator = Paginator(pokemonPaginationRepository, pageSize = 20)
)
```

## Advanced Usage

### Custom Cache Configuration

```kotlin
val paginator = Paginator(
    repository = myRepository,
    pageSize = 50, // Larger page size
    scope = viewModelScope // Custom scope
)
```

### Manual State Management

```kotlin
// Direct paginator usage without ViewModel
val paginator = Paginator(repository, pageSize = 20)

// Observe state
paginator.state.collect { state ->
    // Handle state changes
}

// Handle events
paginator.handleEvent(PaginationEvent.LoadFirstPage)
paginator.handleEvent(PaginationEvent.LoadNextPage)
```

### Custom UI Components

```kotlin
@Composable
fun CustomPaginatedGrid(
    state: PaginationState<MyItem>,
    onLoadMore: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(state.items) { item ->
            MyItemCard(item)
        }
        
        if (state.isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                LoadingIndicator()
            }
        }
    }
    
    // Trigger load more when approaching end
    LaunchedEffect(state.items.size) {
        if (shouldLoadMore()) {
            onLoadMore()
        }
    }
}
```

## API Reference

### PaginationEvent
- `LoadFirstPage`: Load the initial page
- `LoadNextPage`: Load the next page
- `Refresh`: Clear cache and reload from first page
- `Retry`: Retry after an error
- `Clear`: Clear all data and reset state

### PaginationSideEffect
- `ShowError(message)`: Emitted when an error occurs
- `RefreshCompleted`: Emitted when refresh is completed
- `EndReached`: Emitted when no more data is available

### Integration Utilities

#### PaginationIntegration.createFlowAdapter()
Creates a repository adapter for Flow-based repositories.

#### PaginationIntegration.createOffsetAdapter()
Creates a repository adapter for offset-based pagination APIs.

#### PaginationIntegration.createSuspendAdapter()
Creates a repository adapter for simple suspend functions.

## Testing

The library is designed to be easily testable:

```kotlin
@Test
fun `test pagination loading`() = runTest {
    val mockRepository = mockk<PaginationRepository<String>>()
    val paginator = Paginator(mockRepository, pageSize = 10)
    
    coEvery { mockRepository.loadPage(10, 0) } returns PaginationResult.Success(
        items = listOf("item1", "item2"),
        currentPage = 0,
        hasNextPage = true
    )
    
    paginator.handleEvent(PaginationEvent.LoadFirstPage)
    
    val state = paginator.state.first()
    assertEquals(listOf("item1", "item2"), state.items)
    assertFalse(state.isLoading)
}
```

## Dependencies

The library has minimal dependencies and reuses the existing project dependencies:
- Kotlin Coroutines
- AndroidX Lifecycle components
- Existing MVI base classes from `core_ui`

## Migration Guide

To migrate from manual pagination to PagerLib:

1. Create a repository adapter using `PaginationIntegration`
2. Replace your existing ViewModel with `PaginationViewModel`
3. Update your UI to use `PaginatedLazyColumn` or observe the pagination state
4. Remove manual pagination logic from your existing code

The library is designed to minimize changes to existing code while providing powerful pagination capabilities.