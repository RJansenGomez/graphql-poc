package com.manomano.graphql.model

import com.manomano.graphql.model.ActorDataFetcher.Companion.actors
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class PetDataFetcher() {

    companion object {
        val pets = listOf(
            Pet("Toby", PetType.DOG),
            Pet("Kiwi", PetType.CAT),
            Pet("Ekans", PetType.CROCODILE),
            Pet("Kuala", PetType.KOALA)
        )

        private val searchMap = mapOf(
            Pair(actors[0].name, listOf(pets[0])),
            Pair(actors[1].name, listOf(pets[3])),
            Pair(actors[2].name, listOf(pets[1])),
            Pair(actors[3].name, listOf(pets[2])),
            Pair(actors[4].name, emptyList()),
            Pair(actors[5].name, emptyList())
        )

        fun forActor(actorName: String): List<Pet> {
            Thread.sleep(1000)
            return searchMap.getOrDefault(actorName, emptyList())
        }
    }

    @DgsQuery
    fun pets(
        @InputArgument name: String?,
        @InputArgument type: PetType?,
        //@InputArgument owner: ActorDataFetcher.Nationality?
    ): List<Pet> {
        var currentPets = pets
        if (name != null) {
            currentPets = pets.filter { it.name.contains(name) }.toList()
        }
        if (type != null) {
            currentPets = pets.filter { it.type == type }.toList()
        }
        return currentPets
    }

    @DgsData(parentType = "Query", field = "pets")
    fun pets() = pets

    data class Pet(val name: String, val type: PetType)
    enum class PetType {
        DOG,
        CAT,
        FISH,
        CROCODILE,
        KOALA,
    }
}