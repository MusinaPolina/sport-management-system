package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.Reader
import java.io.Writer
import java.time.Duration
import kotlin.math.max

private fun addRecord(record: List<String>, groupName: String) {
    logger.debug { "add a $groupName group's record: $record " }
    val number = getNumberByRecord(record)
    val participant = getParticipantByRecord(record, groupName)
    val result = getResultByRecord(record)
    participantByNumber[number] = participant
    resultByNumber[number] = result
    addLeader(groupName, record, number)
}

private const val NUMBERINDEX = 1
private const val RESULTINDEX = 7
private const val WITHDRAWN = "cнят"
private const val PLACEINDEX = 8

private fun addLeader(groupName: String, record: List<String>, number: Int) {
    if (record[PLACEINDEX] == "1") {
        logger.debug { "added $groupName group's leader: $number" }
        groupLeaders[groupName] = number
    }
}

private fun myTimeConverter(record: String): Long {
    val a = record.split(":")
    return a[2].toLong() + a[1].toLong() * 60 + a[0].toLong() * 3600
}

private fun getResultByRecord(record: List<String>): Duration? {
    return when (record[RESULTINDEX]) {
        WITHDRAWN -> null
        else -> Duration.ofSeconds(myTimeConverter(record[RESULTINDEX]))
    }
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    return Participant(record[3], record[2], record[4].toInt(), record[5], groupName, record[6])
}

private fun getNumberByRecord(record: List<String>): Int {
    return record[NUMBERINDEX].toInt()
}

fun parseInput(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var groupName = ""
    csvParser.forEach { record ->
        when {
            record[0] == "Протокол результатов." || record[0] == "№ п/п" -> Unit
            record[1] == "" -> groupName = record[0]
            else -> addRecord(record.toList(), groupName)
        }
    }
}

fun teamResults(reader: Reader, writer: Writer) {
    resultByNumber.clear()
    groupLeaders.clear()
    participantByNumber.clear()
    parseInput(reader)
    val teamPoints = computeTeamResults().toList().sortedByDescending { it.second }
    printTeamPoint(teamPoints, writer)
}

private fun printTeamPoint(teamPoints: List<Pair<String, Int>>, writer: Writer) {
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
        .withHeader("Место", "Команда", "Результат"))
    teamPoints.forEachIndexed { index, (team, points) ->
        csvPrinter.printRecord(index + 1, team, points)
    }
    csvPrinter.flush()
    csvPrinter.close()
}

private fun computeTeamResults(): MutableMap<String, Int> {
    val teamPoints = mutableMapOf<String, Int>()
    resultByNumber.forEach { (number, result) ->
        val participant = participantByNumber[number]
        require(participant != null) {
            logger.error { "participant is null" }
        }

        val groupLeaderResult = resultByNumber[groupLeaders[participant.group]]

        logger.debug { "$number in team ${participant.team}" }
        val points = computePoints(result, groupLeaderResult)
        logger.debug { "$number in team ${participant.team} points is $points" }

        teamPoints[participant.team] = (teamPoints[participant.team] ?: 0) + points
    }
    return teamPoints
}

fun computePoints(result: Duration?, groupLeaderResult: Duration?) : Int {
    return if (result == null)
        0
    else {
        require(groupLeaderResult != null) {
            logger.error { "leader result is null" }
        }
        max(0, (200 - 100 * result.toSeconds()/groupLeaderResult.toSeconds()).toInt())
    }
}