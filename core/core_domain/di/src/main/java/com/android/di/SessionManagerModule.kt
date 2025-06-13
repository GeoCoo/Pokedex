package com.android.di

import com.android.api.ResourceProvider
import com.android.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SessionManagerModule {
    @Provides
    @Singleton
    fun provideSessionManager(resourceProvider: ResourceProvider): SessionManager {
        return SessionManager(resourceProvider)
    }
}