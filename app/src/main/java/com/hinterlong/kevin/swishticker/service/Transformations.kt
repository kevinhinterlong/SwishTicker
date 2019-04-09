package com.hinterlong.kevin.swishticker.service

import com.hinterlong.kevin.swishticker.service.data.*

data class WinLoss(val wins: Long, val ties: Long, val losses: Long)

fun winLossFromGame(games: List<GameAndActions>, teamId: Long): WinLoss {
    var wins: Long = 0
    var ties: Long = 0
    var losses: Long = 0
    games.forEach {
        val homeScore = it.actions.filter { it.team == teamId }.map(::toPoints).sum()
        val awayScore = it.actions.filterNot { it.team == teamId }.map(::toPoints).sum()
        if (homeScore > awayScore) {
            wins++
        } else if (homeScore == awayScore) {
            ties++
        } else {
            losses++
        }
    }

    return WinLoss(wins, ties, losses)
}

data class GameAndScore(val game: Game, val score: Score)
data class Score(val home: Long, val away: Long)

fun gameScores(gameAndActions: List<GameAndActions>): List<GameAndScore> {
    return gameAndActions.map { gameScore(it.game, it.actions) }.sortedByDescending { it.game.dateCreated }
}

fun gameScore(game: Game, actions: List<Action>): GameAndScore {
    val homeScore = actions.filter { it.team == game.team1 }.map(::toPoints).sum().toLong()
    val awayScore = actions.filter { it.team == game.team2 }.map(::toPoints).sum().toLong()
    return GameAndScore(game, Score(homeScore, awayScore))
}

data class ShotCount(val made: Long = 0, val missed: Long = 0) {
    operator fun plus(other: ShotCount) = ShotCount(made + other.made, missed + other.missed)

    override fun toString(): String {
        return "$made/${made + missed}"
    }
}

data class PlayerStats(val shot1: ShotCount = ShotCount(), val shot2: ShotCount = ShotCount(), val shot3: ShotCount = ShotCount(), val fouls: Long = 0, private val games: Long = 1) {
    operator fun plus(other: PlayerStats) = PlayerStats(
        shot1 + other.shot1,
        shot2 + other.shot2,
        shot3 + other.shot3,
        other.fouls + fouls
    )

    val pointsPerGame: Double
        get() {
            return if (games == 0L) {
                0.0
            } else {
                (1 * shot1.made + 2 * shot2.made + 3 * shot3.made) / games.toDouble()
            }
        }
}

fun getTotalStats(it: Collection<PlayerStats>, games: Long): PlayerStats {
    return it.fold(PlayerStats(), { acc, playerStats -> acc + playerStats }).copy(games = games)
}

fun playerStats(actions: List<Action>, teamId: Long, games: Long = 1): Map<Long?, PlayerStats> {
    val teamActions = actions.filter { it.team == teamId }
    return teamActions.groupBy { it.player }.mapValues {
        it.value.map(::actionToStats).fold(PlayerStats()) { acc, playerStats ->
            acc + playerStats
        }.copy(games = games)
    }
}

fun actionToStats(action: Action) = when (action.actionType) {
    ActionType.FREE_THROW -> PlayerStats(shot1 = actionResultToShotCount(action.actionResult))
    ActionType.TWO_POINT -> PlayerStats(shot2 = actionResultToShotCount(action.actionResult))
    ActionType.THREE_POINT -> PlayerStats(shot3 = actionResultToShotCount(action.actionResult))
    ActionType.FOUL -> PlayerStats(fouls = 1)
}

fun actionResultToShotCount(actionResult: ActionResult) = when (actionResult) {
    ActionResult.SHOT_HIT -> ShotCount(1, 0)
    ActionResult.SHOT_MISS -> ShotCount(0, 1)
    else -> ShotCount(0, 0)
}