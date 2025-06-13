package com.android.pokemons.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.PokemonInteractor
import com.android.api.SamplePartialState
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.model.PokemonDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    val pokemons: List<PokemonDomain>? = listOf()
) : ViewState

sealed class Event : ViewEvent {
    data class GetPokemons(val limit: Int, val offset: Int) : Event()
    data object HandleLoading : Event()
}

sealed class Effect : ViewSideEffect {
    data object SampleEffect : Effect()
}


@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonInteractor: PokemonInteractor
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetPokemons -> {
                viewModelScope.launch {
                    pokemonInteractor.getPokemons(event.limit, event.offset)
                        .collect { partialState ->
                            when (partialState) {
                                is SamplePartialState.Success -> {
                                    setState {
                                        copy(
                                            pokemons =  partialState.pokemons,
                                        )
                                    }
                                }

                                is SamplePartialState.Failed -> {
                                    setState {
                                        copy(isLoading = false)
                                    }
                                }

                                is SamplePartialState.Error -> {
                                    setState {
                                        copy(isLoading = false)
                                    }
                                }
                            }
                        }
                    setState {
                        copy(isLoading = false)
                    }

                }
            }

            is Event.HandleLoading -> {
                setState {
                    copy(isLoading = false)
                }
            }
        }
    }
}