package com.android.di

import com.android.api.ApiClient
import com.android.impl.ApiClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiBindsModule {
    @Binds
    @Singleton
    abstract fun bindApiClient(impl: ApiClientImpl): ApiClient
}