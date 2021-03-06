package org.graphql.model

import com.netflix.dgs.codgen.generated.DgsConstants
import com.netflix.graphql.dgs.*

@DgsComponent
class ShowsDataFetcher {
    companion object {
        private val shows = listOf(
            Show(1, "Stranger Things", 2016),
            Show(2, "Ozark", 2017),
            Show(3, "The Crown", 2016),
            Show(4, "Dead to Me", 2019),
            Show(5, "Orange is the New Black", 2013)

        )
        private val searchMap = mapOf(
            Pair(ActorDataFetcher.actors[0].name, listOf(shows[0], shows[3], shows[4])),
            Pair(ActorDataFetcher.actors[1].name, listOf(shows[0], shows[1], shows[3], shows[4])),
            Pair(ActorDataFetcher.actors[2].name, listOf(shows[0], shows[2], shows[3])),
            Pair(ActorDataFetcher.actors[3].name, listOf(shows[1], shows[2])),
            Pair(ActorDataFetcher.actors[4].name, listOf(shows[1], shows[2])),
            Pair(ActorDataFetcher.actors[5].name, listOf(shows[3], shows[4])),
        )

        fun forActor(actorName: String): List<Show> {
            Thread.sleep(1000)
            return searchMap.getOrDefault(actorName, emptyList())
        }
    }

    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = "shows")
    fun shows() = shows

    @DgsQuery
    fun shows(@InputArgument titleFilter: String?): List<Show> {
        return if (titleFilter != null) {
            filterShowByTitle(shows.toSet(), titleFilter).toList()
        } else {
            shows
        }
    }

    @DgsQuery
    fun showsExtended(@InputArgument titleFilter: String?, @InputArgument actorName: String?): List<Show> {
        var currentShows = shows.toSet()
        if (actorName != null) {
            val currentActors = ActorDataFetcher().actors(actorName, null, null)
            currentShows = currentActors.flatMap { searchMap[it.name]!!.toSet() }.toSet()
        }
        if (titleFilter != null) {
            currentShows = filterShowByTitle(currentShows, titleFilter)
        }
        return currentShows.toList()
    }

    private fun filterShowByTitle(
        currentShows: Set<Show>,
        titleFilter: String
    ): Set<Show> {
        return currentShows.filter { it.title.contains(titleFilter) }.toSet()
    }

    @DgsData(parentType = "Show", field = "actors")
    fun actors(df: DgsDataFetchingEnvironment): List<ActorDataFetcher.Actor> {
        val show: Show = df.getSource()

        return ActorDataFetcher.forShow(show.id)
    }

    data class Show(val id: Int, val title: String, val releaseYear: Int)
}