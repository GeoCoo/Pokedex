# Migration Guide: Retrofit to Ktor Client

This document outlines the changes made to migrate from Retrofit to Ktor Client for KMP compatibility.

## Key Changes

### 1. Data Models
**Before (Retrofit/Gson):**
```kotlin
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonEntryDto>
)
```

**After (KMP/kotlinx.serialization):**
```kotlin
@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonEntryDto>
)
```

### 2. API Interface
**Before (Retrofit):**
```kotlin
interface ApiClient {
    suspend fun getPokemons(limit:Int, offset:Int): Response<PokemonListResponse>
}
```

**After (Ktor):**
```kotlin
interface ApiClient {
    suspend fun getPokemons(limit:Int, offset:Int): Result<PokemonListResponse>
}
```

### 3. Response Handling
**Before (Retrofit Response):**
```kotlin
val response = apiClient.getPokemons(limit, offset)
when {
    response.isSuccessful && response.body() != null -> {
        emit(PokemonsResponse.Success(response.body()))
    }
    else -> {
        emit(PokemonsResponse.Error("Request failed"))
    }
}
```

**After (Kotlin Result):**
```kotlin
val result = apiClient.getPokemons(limit, offset)
result.fold(
    onSuccess = { response ->
        emit(PokemonsResponse.Success(response))
    },
    onFailure = { exception ->
        emit(PokemonsResponse.Error(exception.message ?: "Request failed"))
    }
)
```

### 4. Dependency Injection
**Before (Retrofit modules):**
```kotlin
@Module
class NetworkModule {
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()...
    
    @Provides  
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
```

**After (Ktor modules):**
```kotlin
@Module
class NetworkModule {
    @Provides
    fun provideHttpClient(): HttpClient = createHttpClient()
    
    @Provides
    fun provideKtorApiClient(httpClient: HttpClient): KtorApiClient = KtorApiClient(httpClient)
}
```

## Benefits of Migration

1. **KMP Compatibility**: Ktor Client works across Android, iOS, and other platforms
2. **Better Error Handling**: Using Kotlin's Result type provides more type-safe error handling
3. **Coroutines First**: Ktor is built with coroutines in mind from the ground up
4. **Smaller Binary Size**: No more OkHttp and Retrofit dependencies
5. **Modern Serialization**: kotlinx.serialization is faster and more KMP-friendly than Gson

## Breaking Changes

- Replace all `Response<T>` with `Result<T>`
- Update error handling from `response.isSuccessful` to `result.fold`
- Add `@Serializable` annotations to data models
- Remove Retrofit-specific annotations (`@GET`, `@POST`, etc.)