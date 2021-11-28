package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.time.Duration
import java.time.LocalTime

val participantStart = mutableMapOf<Int, LocalTime>()

val courseByGroup = mutableMapOf<String, String>()

val courseCheckPoints = mutableMapOf<String, List<Int>>()

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

fun groupsParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames[0]
    val course = csvParser.headerNames[1]
    csvParser.forEach {
        courseByGroup[it.get(name)] = it.get(course)
    }
}

fun courseParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames.first()
    val coursesNumbers = csvParser.headerNames.drop(1)
    csvParser.forEach {
        courseCheckPoints[it.get(name)] = it.toList().drop(1).filter { it != "" }.map {
            require(it.toIntOrNull() != null) {
                logger.error { "Check point $it is not Int" }
            }
            it.toInt()
        }
    }
}

fun results(startTimesReader: Reader, splitsReader: Reader) {
    val groupsReader = TODO()
    val coursesReader = TODO()
    startTimeParse(startTimesReader)
    groupsParse(groupsReader)
    courseParse(coursesReader)
}