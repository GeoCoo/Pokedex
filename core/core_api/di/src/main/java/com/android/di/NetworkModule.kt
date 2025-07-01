package com.android.di

import com.android.shared.network.KtorApiClient
import com.android.shared.network.createHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = createHttpClient()

    @Provides
    @Singleton
    fun provideKtorApiClient(httpClient: HttpClient): KtorApiClient = KtorApiClient(httpClient)
}