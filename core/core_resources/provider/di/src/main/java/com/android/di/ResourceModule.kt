package com.android.di

import com.android.api.ResourceProvider
import com.android.impl.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {

    @Binds
    @Singleton
    abstract fun provideResourceProvider(impl: ResourceProviderImpl): ResourceProvider

}