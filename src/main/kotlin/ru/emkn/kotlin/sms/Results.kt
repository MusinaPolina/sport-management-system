package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.Reader
import java.io.Writer
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val participantStart = mutableMapOf<Int, LocalTime>()

private fun addParticipant(record: List<String>, groupName: String) {
    val number = getNumberByRecord(record)
    val participant = getParticipantByRecord(record, groupName)
    val startTime = getStartTimeByRecord(record)
    participantByNumber[number] = participant
    participantStart[number] = startTime
}

private const val NUMBERINDEX = 0
private const val STARTTIMEINDEX = 6

private fun recordLocalTime(time: String): LocalTime {
    return LocalTime.parse(time)
}

private fun getStartTimeByRecord(record: List<String>): LocalTime {
    return recordLocalTime(record[STARTTIMEINDEX])
}

private fun getParticipantByRecord(record: List<String>, groupName: String): Participant {
    return Participant(record[2], record[1], record[3].toInt(), record[4], groupName, record[5])
}

private fun getNumberByRecord(record: List<String>): Int {
    return record[NUMBERINDEX].toInt()
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

private fun getSplitNumberByRecord(record: List<String>): Int {
    val recordNumber = record[NUMBERINDEX]
    if (recordNumber.toIntOrNull() == null) {
        logger.error { "record number $recordNumber is not Int" }
        throw IsNotInt(recordNumber)
    }
    return recordNumber.toInt()
}

private fun updateLeader(number: Int) {
    require(participantByNumber[number] != null) { logger.error { "participant $number is null" } }
    val groupName = participantByNumber[number]?.group!!
    if (!groupLeaders.containsKey(groupName) || resultByNumber[number]!! < resultByNumber[groupLeaders[groupName]!!]) {
        groupLeaders[groupName] = number
    }
}

private fun checkSplitStartFinish(number:Int, splits: List<List<String>>, start: Int, finish: Int) {
    if (splits.last().size == 1 || splits.last().first().toIntOrNull() != finish) {
        splitSplitRecordStartFinishExeption(number, "finish")
    }
    if (splits.first().first().toIntOrNull() != start) {
        splitSplitRecordStartFinishExeption(number, "start")
    }
}

private fun splitSplitRecordStartFinishExeption(number: Int, exception: String) {
    logger.error { "$number participant hasn't $exception record" }
    throw AbsentOfStartFinishRecord(number, exception)
}

private fun addSplits(number:Int, splits: List<List<String>>, start: Int, finish: Int) {
    splits.forEachIndexed { index, courseTime ->
        if (courseTime[0].toIntOrNull() == null) {
            logger.error { "Number of check point should be Int in add splitRecord" }
            throw IsNotInt(courseTime[0])
        }
        val course = courseTime[0].toInt()
        val time = recordLocalTime(courseTime[1])
        checkCourses(number, course, time, index, start, finish)
    }
}

private fun addSplitRecord(record: List<String>, start: Int, finish: Int) {
    if (record.size <= 1) {
        logger.error { "not enough arguments in split record" }
        throw WrongSplit()
    }
    val number = getSplitNumberByRecord(record)
    val splits = record.drop(1).filter { it != "" }.chunked(2)
    checkSplitStartFinish(number, splits, start, finish)
    addSplits(number, splits, start, finish)
}

private fun addSplitFinish(number: Int, time: LocalTime) {
    resultByNumber[number] = Duration.between(participantStart[number], time)
    updateLeader(number)
}


private fun checkCourses(course: Int, groupName: String?, index: Int, number: Int) {
    if (course != config.courseCheckPoints[config.courseByGroup[groupName]]?.get(index - 1)) {
        logger.error { "$number wrong check point" }
        throw WrongCheckPoint(number)
    }
}


private fun checkCourses(number: Int, course: Int, time: LocalTime, index: Int, start: Int, finish: Int) {
    val groupName = participantByNumber[number]?.group
    when (course) {
        start -> checkSplitStart(time, number)
        finish -> addSplitFinish(number, time)
        else -> checkCourses(course, groupName, index, number)
    }
}

private fun checkSplitStart(time: LocalTime, number: Int) {
    if (time != participantStart[number]) {
        logger.error { "$number false start" }
        throw FalseStart(number)
    }
}

private fun splitsParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    val start = config.start
    val finish = config.finish
    csvParser.forEach { addSplitRecord(it.toList(), start, finish) }
}

private fun getGap(start: Duration, finish: Duration): Duration {
    return finish - start
}

//TODO()
private fun addGroupResults(groupName: String, csvPrinter: CSVPrinter) {
    val groupResults = resultByNumber.filter { (number, _) -> participantByNumber[number]?.group == groupName }
        .toList().sortedBy { it.second }.sortedBy { it.second == null }

    groupResults.forEachIndexed { index, (number, _) ->
        val participant = participantByNumber[number]
        require(participant != null) { logger.error { "participant $number is null" } }

        val res = resultByNumber[number]

        val place = if (res == null) null else index + 1

        val gap = if (res == null || place == 1) null else
            getGap(resultByNumber[groupLeaders[groupName]]!!, resultByNumber[number]!!)

        val localTimeGap = if (gap == null) null else (LocalTime.of(0, 0) + gap).format(DateTimeFormatter.ISO_TIME)

        csvPrinter.printRecord(index + 1, number, participant.lastName, participant.firstName,
            participant.yearOfBirth, participant.sportsCategory, participant.team,
            (LocalTime.of(0, 0) + res).format(DateTimeFormatter.ISO_TIME),
            place, localTimeGap)
    }
}

private fun resultsTable(csvPrinter: CSVPrinter) {
    csvPrinter.printRecord("Протокол результатов.")
    config.courseByGroup.forEach { (groupName, _) ->
        csvPrinter.printRecord(groupName)
        csvPrinter.printRecord("№ п/п","Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Результат", "Место", "Отставание")
        addGroupResults(groupName, csvPrinter)
    }
}

fun results(startTimesReader: Reader, splitsReader: Reader, writer: Writer) {
    participantStart.clear()
    participantByNumber.clear()
    resultByNumber.clear()
    groupLeaders.clear()

    startTimeParse(startTimesReader)
    splitsParse(splitsReader)
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
    resultsTable(csvPrinter)
    csvPrinter.flush()
    csvPrinter.close()
}