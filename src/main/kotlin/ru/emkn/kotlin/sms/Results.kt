package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.time.Duration
import java.time.LocalTime

val participantStart = mutableMapOf<Int, LocalTime>()

fun addParticipant(record: List<String>, groupName: String) {
    val number = getNumberByRecord(record)
    val participant = getParticipantByRecord(record, groupName)
    val startTime = getStartTimeByRecord(record)
    participantByNumber[number] = participant
    participantStart[number] = startTime
}

private const val NUMBERINDEX = 0
private const val STARTTIMEINDEX = 6

private fun getStartTimeByRecord(record: List<String>): LocalTime {
    return TODO()
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    return Participant(record[2], record[1], record[3].toInt(), record[4], groupName, record[5])
}

private fun getNumberByRecord(record: List<String>): Int {
    return record[NUMBERINDEX].toInt()
}

fun startTimeParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var groupName = ""
    csvParser.forEach { record ->
        when {
            record[1] == "" -> groupName = record[0]
            record[0] == "Номер" -> null
            else -> addParticipant(record.toList(), groupName)
        }
    }
}

fun results(startTimesReader: Reader, splitsReader: Reader) {
    val groupsReader = TODO()
    val coursesReader = TODO()
    startTimeParse(startTimesReader)
}