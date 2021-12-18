package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer
import java.time.Duration
import kotlin.math.max

class RowTeamsResults {
    val teamsPoint: List<Pair<Team, Int>>

    init {
        teamsPoint = computeTeamResults(results)
    }

    fun exportCSV(writer: Writer) {
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withTrim()
            .withHeader("Место", "Команда", "Результат"))
        teamsPoint.forEachIndexed { index, (team, points) ->
            csvPrinter.printRecord(index + 1, team.name, points)
        }
        csvPrinter.flush()
        csvPrinter.close()
    }

    private fun computeTeamResults(results: List<RowResult>): List<Pair<Team, Int>> {
        val teamPoints = mutableMapOf<Team, Int>()
        results.forEach { result ->
            val participant = result.participant

            val groupLeaderResult = participant.group.leaderResult?.time

            val points = computePoints(result.result?.time, groupLeaderResult)

            teamPoints[participant.team] = (teamPoints[participant.team] ?: 0) + points
        }
        return teamPoints.toList().sortedByDescending { it.second }
    }

    private fun computePoints(result: Duration?, groupLeaderResult: Duration?) : Int {
        return if (result != null)  {
            require(groupLeaderResult != null) {
                logger.error { "leader result is null" }
            }
            max(0, (200 - 100 * result.toSeconds()/groupLeaderResult.toSeconds()).toInt())
        } else 0
    }
}