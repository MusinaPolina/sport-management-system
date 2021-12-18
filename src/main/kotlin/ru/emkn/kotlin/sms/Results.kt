package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.io.Writer
import java.time.LocalTime
import java.time.format.DateTimeParseException

private fun addParticipant(record: List<String>, groupName: String) {
    participants.add(getParticipantByRecord(record, groupName))
    startTimes.add(ParticipantStartTime(participants.last(), getStartTimeByRecord(record)))
}

private const val NUMBERINDEX = 0
private const val STARTTIMEINDEX = 6

private fun getStartTimeByRecord(record: List<String>): LocalTime {
    try {
        return LocalTime.parse(record[STARTTIMEINDEX])
    } catch (e: DateTimeParseException) {
        throw IsNotLocalTime(record[STARTTIMEINDEX])
    }
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    val team = addTeam(record[5])
    return Participant(record[0].toInt(), record[2], record[1], record[3].toInt(), record[4], groupName, team)
}

private fun startTimeParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var groupName = ""
    csvParser.forEach { record ->
        when {
            record.toList().size == 1 -> groupName = record[0]
            record[0] == "Номер" -> Unit
            else -> addParticipant(record.toList(), groupName)
        }
    }
}

private fun checkParticipant(record: List<String>): Boolean {
    return checkParticipantByRecord(record) && checkStartTimeByRecord(record)
}


private fun checkStartTimeByRecord(record: List<String>): Boolean {
    return try {
        LocalTime.parse(record[STARTTIMEINDEX])
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

private fun checkParticipantByRecord(record: List<String>): Boolean {
    if (record.size < 6) return false
    if (record[0].toIntOrNull() == null) return false
    if (record[3].toIntOrNull() == null) return false
    return true
}

private fun checkStartTimeHeader(header: List<String>): Boolean {
    return header == listOf("Номер","Фамилия","Имя","Г.р.","Разр","Команда","Время старта")
}

fun checkStartTime(reader: Reader): Boolean {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    var previousGroupName = false
    return csvParser.all { record ->
        when {
            record.toList().isEmpty() -> false
            record.toList().size == 1 -> {
                previousGroupName = true
                true
            }
            previousGroupName -> {
                previousGroupName = false
                checkStartTimeHeader(record.toList())
            }
            else -> checkParticipant(record.toList())
        }
    }
}


private fun checkSplitNumberByRecord(record: List<String>) = record[NUMBERINDEX].toIntOrNull() != null

private fun checkRace(record: List<String>): Boolean {
    if (record.isEmpty()) return false
    record.drop(1).chunked(2).forEach {
        val number = it[0]
        val time = it[1]
        if (number.toIntOrNull() == null) return false
        try {
            LocalTime.parse(time)
        } catch (e: DateTimeParseException) {
            return false
        }
    }
    return true
}

private fun checkSplitRecord(record: List<String>) : Boolean {
    return when (record.isEmpty()) {
        true -> false
        else -> checkSplitNumberByRecord(record) && checkRace(record)
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

private fun getRaceByRecord(record: List<String>): Race {
    return Race(record.drop(1).filter { it != "" }.chunked(2).map {
        CheckPoint(it[0].toInt(), LocalTime.parse(it[1]))
    })
}

private fun addSplitRecord(record: List<String>) {
    if (record.isEmpty()) {
        logger.error { "not enough arguments in split record" }
        throw WrongSplit()
    }
    val number = getSplitNumberByRecord(record)
    val race = getRaceByRecord(record)
    splits.add(Split(number, race))
}


fun checkSplits(reader: Reader): Boolean {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    return csvParser.all { checkSplitRecord(it.toList()) }
}

private fun splitsParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    csvParser.forEach { addSplitRecord(it.toList()) }
}

fun buildResults() {
    groups.forEach {
        it.computeResult()
    }
}

fun results(startTimesReader: Reader, splitsReader: Reader, writer: Writer) {
    startTimeParse(startTimesReader)
    splitsParse(splitsReader)
    buildResults()
    RowResults().exportCSV(writer)
}