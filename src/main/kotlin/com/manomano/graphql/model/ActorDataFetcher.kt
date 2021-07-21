package com.manomano.graphql.model

import com.manomano.graphql.model.ActorDataFetcher.Nationality.*
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ActorDataFetcher {

    companion object {
        val actors = listOf(
            Actor("Peter griffin", GB, 1000.00F),
            Actor("Vigo mortersen", FR, 1023.45F),
            Actor("Harry Potter", ES, 1800.00F),
            Actor("Marco Pascuale", IT, 1900.00F),
            Actor("Alberto Pino", ES, 2100.00F),
            Actor("Ramon Jansen", NL, 700.80F)
        )

        private val searchMap = mapOf(
            Pair(1, listOf(actors[0], actors[1], actors[2])),
            Pair(2, listOf(actors[3], actors[1], actors[4])),
            Pair(3, listOf(actors[2], actors[4], actors[3])),
            Pair(4, listOf(actors[1], actors[5], actors[2])),
            Pair(5, listOf(actors[0], actors[1], actors[5])),
        )

        fun forShow(showId: Int): List<Actor> {
            Thread.sleep(1000)
            return searchMap.getOrDefault(showId, emptyList())
        }
    }

    @DgsQuery
    fun actors(
        @InputArgument name: String?,
        @InputArgument salaryRange: SalaryRange?,
        @InputArgument nationality: Nationality?
    ): List<Actor> {
        var currentActors = actors
        if (name != null) {
            currentActors = actors.filter { filterName(it, name) }.toList()
        }
        if (salaryRange != null) {
            currentActors = actors.filter { filterSalaryRange(it, salaryRange) }.toList()
        }
        if (nationality != null) {
            currentActors = actors.filter { filterNationality(it, nationality) }.toList()
        }
        return currentActors
    }

    @DgsData(parentType = "Query", field = "actors")
    fun actors() = actors

    private fun filterName(actor: Actor, name: String) = actor.name.contains(name)

    private fun filterNationality(actor: Actor, nationality: Nationality) = actor.nationality == nationality

    private fun filterSalaryRange(actor: Actor, salaryRange: SalaryRange): Boolean {
        if (salaryRange.start != null && salaryRange.end != null) {
            return actor.salary in salaryRange.start..salaryRange.end
        } else if (salaryRange.start != null) {
            return salaryRange.start < actor.salary
        } else if (salaryRange.end != null) {
            return salaryRange.end > actor.salary
        }
        return true;

    }

    data class Actor(val name: String, val nationality: Nationality, val salary: Float)

    data class SalaryRange(val start: Float?, val end: Float?)

    enum class Nationality { ES, FR, GB, IT, NL }

}