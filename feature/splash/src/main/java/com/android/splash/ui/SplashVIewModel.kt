package com.android.splash.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,

) : ViewState

sealed class Event : ViewEvent {
    data object PrepareData : Event()
}

sealed class Effect : ViewSideEffect {
    data object SampleEffect : Effect()
}


@HiltViewModel
class SplashViewModel @Inject constructor(
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.PrepareData -> {
                viewModelScope.launch {
                    setState {
                        copy(isLoading = false)
                    }
                    setEffect {
                        Effect.SampleEffect
                    }
                }
            }
        }
    }
}