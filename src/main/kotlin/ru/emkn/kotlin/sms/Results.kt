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
    return TODO("string to local time")
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

private fun getSplitNumberByRecord(record: List<String>): Int {
    val recordNumber = record[NUMBERINDEX]
    if (recordNumber.toIntOrNull() == null) {
        logger.error { "record number $recordNumber is not Int" }
        throw IsNotInt(recordNumber)
    }
    return recordNumber.toInt()
}

fun addSplitRecord(record: List<String>, start: Int, finish: Int) {
    if (record.size <= 1) {
        TODO("Write exception")
    }
    val number = getSplitNumberByRecord(record)
    val splits = record.drop(1).filter { it != "" }.chunked(2)
    if (splits.last().size == 1 || splits.last().first().toIntOrNull() != finish) {
        logger.error { "$number participant hasn't finish record" }
        throw AbsentOfStartFinishRecord(number, "finish")
    }
    if (splits.first().first().toIntOrNull() != start) {
        logger.error { "$number participant hasn't start record" }
        throw AbsentOfStartFinishRecord(number, "start")
    }
    splits.forEach {

    }
}

fun splitsParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    val start = config.start
    val finish = config.finish
    csvParser.forEach { addSplitRecord(it.toList(), start, finish) }
}

fun results(startTimesReader: Reader, splitsReader: Reader) {
    val groupsReader = config.groups
    val coursesReader = config.courses
    startTimeParse(startTimesReader)
    groupsParse(groupsReader)
    courseParse(coursesReader)
    splitsParse(splitsReader)
}