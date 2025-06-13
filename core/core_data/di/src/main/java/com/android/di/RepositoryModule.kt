package com.android.di


import com.android.api.PokemonsRepository
import com.android.impl.PokemonsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun providePokemonsRepository(impl: PokemonsRepositoryImpl): PokemonsRepository

}

