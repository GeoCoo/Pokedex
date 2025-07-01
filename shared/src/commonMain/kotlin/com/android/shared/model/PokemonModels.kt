package com.android.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokemonEntryDto>
)

@Serializable
data class PokemonEntryDto(
    val name: String,
    val url: String
)