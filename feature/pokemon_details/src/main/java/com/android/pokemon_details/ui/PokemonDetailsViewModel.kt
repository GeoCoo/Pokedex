package com.android.pokemon_details.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.PokemonInteractor
import com.android.api.SinglePokemonPartialState
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.model.SinglePokemonDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    val pokemonData: SinglePokemonDomain? = null
) : ViewState

sealed class Event : ViewEvent {
    data class GetPokemonDetails(val name: String) : Event()

}

sealed class Effect : ViewSideEffect {
}


@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val pokemonInteractor: PokemonInteractor
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetPokemonDetails -> {
                viewModelScope.launch {
                    pokemonInteractor.getSinglePokemon(event.name)
                        .collect { partialState ->
                            when (partialState) {
                                is SinglePokemonPartialState.Success -> {
                                    setState {
                                        copy(
                                            pokemonData = partialState.singlePokemon,
                                            isLoading = false,

                                            )
                                    }
                                }

                                is SinglePokemonPartialState.Failed -> {
                                    setState {
                                        copy(
                                            isLoading = false,
                                        )
                                    }
                                }

                                is SinglePokemonPartialState.Error -> {
                                    setState {
                                        copy(
                                            isLoading = false,
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}