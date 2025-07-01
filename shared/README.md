# Shared KMP Module

This module contains the shared Kotlin Multiplatform code for the Pokedex app, including:

## Features

- **Networking**: Ktor Client for cross-platform HTTP requests
- **Serialization**: kotlinx.serialization for JSON handling  
- **Models**: Shared data models with @Serializable annotations

## Structure

- `commonMain/`: Shared code for all platforms
- `androidMain/`: Android-specific implementations
- `iosMain/`: iOS-specific implementations

## Dependencies

- Ktor Client for HTTP networking
- kotlinx.serialization for JSON serialization
- kotlinx.coroutines for async operations

## Migration from Retrofit

This module replaces the previous Retrofit-based networking layer to enable KMP compatibility:

- `ApiService` (Retrofit) → `KtorApiClient` (Ktor)
- `Response<T>` (Retrofit) → `Result<T>` (Kotlin stdlib)
- `@SerializedName` (Gson) → `@Serializable` (kotlinx.serialization)