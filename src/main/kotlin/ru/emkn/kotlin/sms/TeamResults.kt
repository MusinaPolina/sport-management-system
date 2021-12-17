package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.io.Writer
import java.time.Duration

private fun parseRecord(record: List<String>, groupName: String) {
    logger.debug { "add a $groupName group's record: $record " }
    val number = getNumberByRecord(record)
    val participant = getParticipantByRecord(record, groupName)
    val participantResult = getResultByRecord(record)
    updateLeader(participant.group, participantResult)
    results.add(RowResult(number, participant, participantResult))
}

private const val NUMBERINDEX = 1
private const val RESULTINDEX = 7
const val WITHDRAWN = "снят"
private const val PLACEINDEX = 8
private const val DELAYINDEX = 9

private fun updateLeader(group: Group, participantResult: ParticipantResult?) {
    if (participantResult != null && participantResult.place == 1) {
        group.leaderResult = participantResult
    }
}

private fun myTimeConverter(record: String): Long? {
    if (record.isEmpty()) return null
    val a = record.split(":")
    return a[2].toLong() + a[1].toLong() * 60 + a[0].toLong() * 3600
}

private fun getResultByRecord(record: List<String>): ParticipantResult? {
    return when (record[RESULTINDEX]) {
        WITHDRAWN -> null
        else -> ParticipantResult(
            myTimeConverter(record[RESULTINDEX])?.let { Duration.ofSeconds(it) } ?: run {
                logger.error { "${record[NUMBERINDEX]} participant time is incorrect" }
                throw WrongTime()
            },
            record[PLACEINDEX].toInt(),
            myTimeConverter(record[DELAYINDEX])?.let { Duration.ofSeconds(it) }
        )
    }
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    val team = addTeam(record[6])
    return Participant(record[3], record[2], record[4].toInt(), record[5], groupName, team)
}

private fun getNumberByRecord(record: List<String>): Int {
    return record[NUMBERINDEX].toInt()
}

private fun parseResultsProtocol(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var groupName = ""
    csvParser.forEach { record ->
        when {
            record[0] == "Протокол результатов." || record[0] == "№ п/п" -> Unit
            record.size() == 1 || record[1] == "" -> groupName = record[0]
            else -> parseRecord(record.toList(), groupName)
        }
    }
}

fun teamResults(reader: Reader, writer: Writer) {
    resultByNumber.clear()
    groupLeaders.clear()
    participantByNumber.clear()
    parseResultsProtocol(reader)
    RowTeamsResults().exportCSV(writer)
}

