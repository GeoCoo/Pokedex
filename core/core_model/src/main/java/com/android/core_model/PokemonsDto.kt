package com.android.core_model

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonEntryDto>
)

data class PokemonEntryDto(
    val name: String,
    val url: String
)