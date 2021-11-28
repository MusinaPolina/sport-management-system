package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.Reader
import java.io.Writer
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private fun readApplication(reader: Reader) : List<Participant> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withHeader("Группа", "Фамилия", "Имя", "Г.р.", "Разр."))
    val team = csvParser.first().get(0)
    logger.debug { "reading application for team $team" }
    val participants = csvParser.drop(1).map { csvRecord ->
        require(csvRecord.get("Г.р.").toIntOrNull() != null) {
            logger.error { "Г.р. isn't a number: ${csvRecord.get("Г.р.")}" }
        }
        Participant(
            csvRecord.get("Имя"),
            csvRecord.get("Фамилия"),
            csvRecord.get("Г.р.").toInt(),
            csvRecord.get("Разр."),
            csvRecord.get("Группа"),
            team,
        )
    }
    return participants
}

private fun drawLots(list: List<Participant>) : List<Int> {
    val shuffle = list.shuffled()
    val startNumber = lastNumber + 1
    shuffle.forEachIndexed { index, participant ->
        lastNumber++
        numberToParticipant[lastNumber] = participant
        numberToStart[lastNumber] = Duration.ofMinutes(index.toLong())
    }
    return (startNumber..lastNumber).toList()
}

private fun printGroup(group: String, numbers: List<Int>, csvPrinter: CSVPrinter) {
    val startTime = LocalTime.of(12, 0, 0)
    csvPrinter.printRecord(group)
    csvPrinter.printRecord("Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Время старта")
    logger.debug { "printing group $group" }
    numbers.forEach { number ->
        val participant = numberToParticipant[number]
        val time = numberToStart[number]
        require(participant != null) {
            logger.error { "wrong number $number" }
        }
        require(time != null) {
            logger.error { "wrong number $number" }
        }
        csvPrinter.printRecord(number, participant.lastName, participant.firstName,
            participant.yearOfBirth, participant.sportsCategory, participant.team,
            (startTime + time).format(DateTimeFormatter.ISO_TIME),
        )
    }
    return
}

private val numberToParticipant : MutableMap<Int, Participant> = mutableMapOf()
private val numberToStart : MutableMap<Int, Duration> = mutableMapOf()
private var lastNumber = 100

fun applicationsToStart (readers: List<Reader>, writer: Writer) {
    lastNumber = 100
    numberToParticipant.clear()
    numberToStart.clear()

    val participants = readers.flatMap { reader -> readApplication(reader) }
    val groups = participants.groupBy { it.group }
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
    groups.forEach { (group, list) ->
        logger.debug { "drawing lots for $group" }
        val numbers = drawLots(list)
        printGroup(group, numbers, csvPrinter)
    }
    csvPrinter.flush()
    csvPrinter.close()
    return
}