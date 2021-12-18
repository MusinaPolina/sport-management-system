package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.io.Writer
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeParseException
import javax.swing.text.StyledEditorKit

private fun parseRecord(record: List<String>, groupName: String) {
    logger.debug { "add a $groupName group's record: $record " }
    val participant = getParticipantByRecord(record, groupName)
    val participantResult = getResultByRecord(record)
    updateLeader(participant.group, participantResult)
    results.add(RowResult(participant, participantResult))
}

private fun checkRecord(record: List<String>): Boolean {
    return checkParticipantByRecord(record) && checkResultByRecord(record)
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

private fun checkResultByRecord(record: List<String>): Boolean {
    if (record.size < RESULTINDEX) return false
    if (record[RESULTINDEX] == WITHDRAWN) return record.drop(RESULTINDEX + 1).all { it.isEmpty() }

    if (record.size < PLACEINDEX) return false
    if (record[PLACEINDEX] == "1") return record.drop(PLACEINDEX + 1).all { it.isEmpty() }

    if (record.size < DELAYINDEX) return false
    return try {
        LocalTime.parse(record[DELAYINDEX])
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    val team = addTeam(record[6])
    return Participant(record[1].toInt(), record[3], record[2], record[4].toInt(), record[5], groupName, team)
}

private fun checkParticipantByRecord(record: List<String>): Boolean {
    if (record.size < 7) return false
    if (record[1].toIntOrNull() == null) return false
    if (record[4].toIntOrNull() == null) return false
    return true
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

private fun checkProtocolHeader(header: List<String>): Boolean {
    return header == listOf("№ п/п","Номер","Фамилия","Имя","Г.р.","Разр","Команда","Результат","Место","Отставание")
}

fun checkResultsProtocol(reader: Reader): Boolean {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim()).toList()

    if (csvParser.isEmpty()) return false
    if (csvParser.first().toList() != listOf("Протокол результатов.")) return false

    var previousGroupName = false
    return csvParser.drop(1).all { record ->
        when {
            record.toList().isEmpty() -> false
            record.toList().size == 1 -> {
                previousGroupName = true
                true
            }
            previousGroupName -> {
                previousGroupName = false
                checkProtocolHeader(record.toList())
            }
            else -> checkRecord(record.toList())
        }
    }
}

fun teamResults(reader: Reader, writer: Writer) {
    parseResultsProtocol(reader)
    RowTeamsResults().exportCSV(writer)
}

