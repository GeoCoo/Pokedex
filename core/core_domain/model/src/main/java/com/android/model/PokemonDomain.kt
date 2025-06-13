package com.android.model

import com.android.core_model.PokemonEntryDto

data class PokemonDomain(
    val name: String,
    val url: String
)


fun PokemonEntryDto.toDomain(): PokemonDomain {
    return PokemonDomain(
        name = this.name,
        url = this.url.createPokemonUrl()
    )
}

fun String.createPokemonUrl(): String {
    val index = this.split("/".toRegex()).dropLast(1).last()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$index.png"
}