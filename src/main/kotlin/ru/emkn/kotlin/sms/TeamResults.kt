package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.time.Duration


val participantByNumber = mutableMapOf<Int, Participant>()
val resultByNumber = mutableMapOf<Int, Duration?>()
val groupLeaders = mutableMapOf<String, Int>()

fun addRecord(record: List<String>, groupName: String) {
    logger.debug { "add a $groupName group's record: $record " }
    val number = getNumberByRecord(record)
    val participant = getParticipantByRecord(record, groupName)
    val result = getResultByRecord(record)
    participantByNumber[number] = participant
    resultByNumber[number] = result
    addLeader(groupName, record, number)
}

const val NUMBERINDEX = 1
const val RESULTINDEX = 7
const val WITHDRAWN = "снят"
const val PLACEINDEX = 8

fun addLeader(groupName: String, record: List<String>, number: Int) {
    if (record[PLACEINDEX] == "1") {
        logger.debug { "added $groupName group's leader: $number" }
        groupLeaders[groupName] = number
    }
}

fun myTimeConverter(record: String): Long {
    val a = record.split(":")
    return a[2].toLong() + a[1].toLong() * 60 + a[0].toLong() * 3600
}

fun getResultByRecord(record: List<String>): Duration? {
    return when (record[RESULTINDEX]) {
        WITHDRAWN -> null
        else -> Duration.ofSeconds(myTimeConverter(record[RESULTINDEX]))
    }
}

fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    return Participant(record[3], record[2], record[4].toInt(), record[5], groupName, record[6])
}

fun getNumberByRecord(record: List<String>): Int {
    return record[NUMBERINDEX].toInt()
}

fun parseInput(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var groupName = ""
    csvParser.forEach { record ->
        when {
            record[0] == "Протокол результатов." || record[0] == "№ п/п" -> null
            record[1] == "" -> groupName = record[0]
            else -> addRecord(record.toList(), groupName)
        }
    }
}

fun teamResults(reader: Reader) {
    parseInput(reader)

}