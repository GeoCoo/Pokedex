package com.android.di


import com.android.api.PokemonInteractor
import com.android.impl.PokemonInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class InteractorModule {

    @Binds
    abstract fun bindSampleInteractor(impl: PokemonInteractorImpl): PokemonInteractor

}
