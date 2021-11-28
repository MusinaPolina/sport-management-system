package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.Reader
import java.io.Writer
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

fun recordLocalTime(time: String): LocalTime {
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
    //val coursesNumbers = csvParser.headerNames.drop(1)
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

fun updateLeader(number: Int) {
    require(participantByNumber[number] == null) { "participant $number is null" }
    val groupName = participantByNumber[number]?.group!!
    if (!groupLeaders.containsKey(groupName) || resultByNumber[number]!! < resultByNumber[groupLeaders[groupName]!!]) {
        groupLeaders[groupName] = number
    }
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
    splits.forEachIndexed { index, courseTime ->
        val course = courseTime[0].toInt() //TODO("Int exception")
        val time = recordLocalTime(courseTime[1]) //TODO("time check exception")
        val groupName = participantByNumber[number]?.group
        when (course) {
            start -> {
                if (time != participantStart[number]) {
                    logger.error { "$number false start" }
                    throw FalseStart(number)
                }
            }
            finish -> {
                resultByNumber[number] = Duration.between(participantStart[number], time)
                updateLeader(number)
            }
            else -> {
                if (course != courseCheckPoints[groupName]?.get(index - 1)) {
                    logger.error { "$number wrong check point" }
                    throw WrongCheckPoint(number)
                }
            }
        }
    }
}

fun splitsParse(reader: Reader) {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
    val start = config.start
    val finish = config.finish
    csvParser.forEach { addSplitRecord(it.toList(), start, finish) }
}

fun getGap(start: Duration, finish: Duration): Duration {
    return finish - start
}

fun addGroupResults(groupName: String, csvPrinter: CSVPrinter) {
    val groupResults = resultByNumber.filter { (number, _) -> participantByNumber[number]?.group == groupName }
        .toList().sortedBy { it.second }.sortedBy { it.second == null }

    groupResults.forEachIndexed { index, (number, _) ->
        val participant = participantByNumber[number]
        require(participant != null) { "participant $number is null" }

        val res = resultByNumber[number]

        val place = if (res == null) null else index + 1

        val gap = if (res == null || place == 1) null else
            getGap(resultByNumber[groupLeaders[groupName]]!!, resultByNumber[number]!!)

        val localTimeGap = LocalTime.of(0, 0) + gap

        csvPrinter.printRecord(index + 1, number, participant.lastName, participant.firstName,
            participant.yearOfBirth, participant.sportsCategory, participant.team, res, place, '+', localTimeGap)
    }
}

fun resultsTable(csvPrinter: CSVPrinter) {
    csvPrinter.printRecord("Протокол результатов.")
    courseByGroup.forEach { (groupName, _) ->
        csvPrinter.printRecord(groupName)
        csvPrinter.printRecord("№ п/п","Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Результат", "Место", "Отставание")
        addGroupResults(groupName, csvPrinter)
    }
}

fun results(startTimesReader: Reader, splitsReader: Reader, writer: Writer) {
    val groupsReader = config.groups
    val coursesReader = config.courses
    startTimeParse(startTimesReader)
    groupsParse(groupsReader)
    courseParse(coursesReader)
    splitsParse(splitsReader)
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
    resultsTable(csvPrinter)
}