package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.Reader
import java.io.Writer
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun applicationsToStart (readers: List<Reader>, writer: Writer) {
    val application = Application()
    application.readApplications(readers)
    application.exportCSV(writer)
}

class Application {
    private fun readApplication(reader: Reader) : List<Participant> {
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT
            .withHeader("Группа", "Фамилия", "Имя", "Г.р.", "Разр."))
        val team = addTeam(csvParser.first().get(0))
        logger.debug { "reading application for team $team" }
        if (csvParser.first().joinToString(",") != "Группа,Фамилия,Имя,Г.р.,Разр.") {
            logger.error { "Wrong application format $team, line 2" }
            throw WrongApplication(team.name, 2)
        }
        csvParser.forEach { csvRecord ->
            if (csvRecord.size() < csvParser.headerNames.size) {
                logger.error { "Application $team, line ${csvRecord.recordNumber}, not enough arguments" }
                throw WrongApplication(team.name, csvRecord.recordNumber)
            }
            if (csvRecord.get("Г.р.").toIntOrNull() == null) {
                logger.error { "Г.р. isn't a number: ${csvRecord.get("Г.р.")}" }
                throw WrongApplication(team.name, csvRecord.recordNumber)
            }
            if (groups.find { it.name == csvRecord.get("Группа")} == null) {
                logger.error { "Application $team, line ${csvRecord.recordNumber}, wrong group" }
                throw WrongApplication(team.name, csvRecord.recordNumber)
            }
            participants.add(Participant(
                lastNumber++,
                csvRecord.get("Имя"),
                csvRecord.get("Фамилия"),
                csvRecord.get("Г.р.").toInt(),
                csvRecord.get("Разр."),
                csvRecord.get("Группа"),
                team,
            ))
        }
        return team.participants
    }

    private fun checkApplication(reader: Reader) : Boolean {
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT
            .withHeader("Группа", "Фамилия", "Имя", "Г.р.", "Разр."))
        if (csvParser.first().get(0).isEmpty()) return false
        if (csvParser.first().joinToString(",") != "Группа,Фамилия,Имя,Г.р.,Разр.") {
            return false
        }
        csvParser.forEach { csvRecord ->
            if (csvRecord.size() < csvParser.headerNames.size) return false
            if (csvRecord.get("Г.р.").toIntOrNull() == null) return false
            if (groups.find { it.name == csvRecord.get("Группа")} == null) return false
        }
        return true
    }

    private var lastNumber = 100

    fun readApplications(readers: List<Reader>) {
        lastNumber = 100
        readers.flatMap { reader -> readApplication(reader) }
    }

    fun checkApplications(readers: List<Reader>): Boolean {
        return readers.all {  checkApplication(it) }
    }


    fun exportCSV(writer: Writer) {
        val groups = participants.groupBy { it.group }
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
        groups.forEach { (group, list) ->
            logger.debug { "drawing lots for $group" }
            drawLots(list)
            printGroup(group.name, csvPrinter)
        }
        csvPrinter.flush()
        csvPrinter.close()
    }

    private fun drawLots(list: List<Participant>) {
        val shuffle = list.shuffled()
        shuffle.forEachIndexed { index, participant ->
            startTimes.add(ParticipantStartTime(participant, (LocalTime.of(0, 0) + Duration.ofMinutes(index.toLong()))))
        }
    }

    private fun printGroup(group: String, csvPrinter: CSVPrinter) {
        csvPrinter.printRecord(group)
        csvPrinter.printRecord("Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Время старта")
        logger.debug { "printing group $group" }
        participants.filter { it.group.name == group }.sortedBy {
                participant -> startTimes.find { it.participant ==  participant}?.time
        }.forEach { participant ->
            val time = startTimes.find { it.participant == participant } ?: throw AbsentOfStartTime(participant.number)
            csvPrinter.printRecord(
                participant.number, participant.lastName, participant.firstName,
                participant.yearOfBirth, participant.sportsCategory, participant.team.name,
                (time.time + Duration.ofHours(12)).format(DateTimeFormatter.ISO_TIME),
            )
        }
    }
}