package com.android.di

import com.android.api.ApiClient
import com.android.api_service.ApiService
import com.android.impl.ApiClientImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiProvidesModule {
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiBindsModule {
    @Binds
    @Singleton
    abstract fun bindApiClient(impl: ApiClientImpl): ApiClient
}