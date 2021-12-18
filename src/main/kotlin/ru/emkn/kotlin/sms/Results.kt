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

/*private fun updateLeader(number: Int) {
    require(participantByNumber[number] != null) { logger.error { "participant $number is null" } }
    val groupName = participantByNumber[number]?.group!!
    if (!groupLeaders.containsKey(groupName.name) ||
        resultByNumber[number]!! < resultByNumber[groupLeaders[groupName.name]!!]) {
        groupLeaders[groupName.name] = number
    }
}*/

/*private fun checkSplitStartFinish(splits: List<List<String>>, start: Int, finish: Int): Boolean {
    if (splits.isEmpty()) {
        return true
    }
    if (splits.last().size == 1 || splits.last().first().toIntOrNull() != finish) {
        return true
        //splitSplitRecordStartFinishException(number, "finish")
    }
    if (splits.first().first().toIntOrNull() != start) {
        return true
        //splitSplitRecordStartFinishException(number, "start")
    }
    return false
}*/

/*private fun splitSplitRecordStartFinishExeption(number: Int, exception: String) {
    logger.error { "$number participant hasn't $exception record" }
    throw AbsentOfStartFinishRecord(number, exception)
}*/

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

/*private fun addSplitFinish(number: Int, time: LocalTime) {
    resultByNumber[number] = Duration.between(participantStart[number], time)
    updateLeader(number)
}*/


/*private fun checkCourses(course: Int, groupName: String, index: Int, number: Int): Boolean {
    if (course != getCourse(groupName).checkPoints[index - 1]) {
    //if (course != config.courseCheckPoints[config.courseByGroup[groupName]]?.get(index - 1)) {
        logger.error { "$number wrong check point" }
        return true
        //throw WrongCheckPoint(number)
    }
    return false
}*/


/*private fun checkCourses(number: Int, course: Int, time: LocalTime, index: Int, start: Int, finish: Int): Boolean {
    val groupName = participantByNumber[number]?.group?.name ?: return true
    when (course) {
        start -> checkSplitStart(time, number)
        finish -> addSplitFinish(number, time)
        else -> {
            if (checkCourses(course, groupName, index, number)) return true
        }
    }
    return false
}*/

/*    splits.find { it.number == participant.number }?.let {
        require(it.race?.start == CheckPoint(config.start, startTime)) {
            throw FalseStart(it.number)
        }
    }*/

/*private fun checkSplitStart(time: LocalTime, number: Int) {
    if (time != participantStart[number]) {
        logger.error { "$number false start" }
        throw FalseStart(number)
    }
}*/

private fun checkParse(reader: Reader): Boolean {
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
  /*  if (!checkParse(splitsReader)) {
        throw WrongCSV("splits")
       TODO("UNCOMMENT")
    }*/
    splitsParse(splitsReader)
    buildResults()
    RowResults().exportCSV(writer)
}