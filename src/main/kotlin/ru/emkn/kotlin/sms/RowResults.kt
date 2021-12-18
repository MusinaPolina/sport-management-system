package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer
import java.time.Duration

data class ParticipantResult(val time: Duration, val place: Int, val delay: Duration?)

class RowResult(val participant: Participant, val result: ParticipantResult?) // null result - withdrawn

val results = mutableListOf<RowResult>()

class RowResults {

    fun exportCSV(writer: Writer) {
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
        resultsTable(csvPrinter)
        csvPrinter.flush()
        csvPrinter.close()
    }

    private fun resultsTable(csvPrinter: CSVPrinter) {
        csvPrinter.printRecord("Протокол результатов.")
        groups.forEach {
            addGroupResults(it, csvPrinter)
        }
    }

    private fun getGap(start: Duration, finish: Duration): Duration {
        return finish - start
    }

    private fun addGroupResults(group: Group, csvPrinter: CSVPrinter) {
        TODO()
        /*val groupResults = results.filter { it.participant.group == group }
        if (groupResults.isEmpty()) {
            return
        }
        csvPrinter.printRecord(groupName)
        csvPrinter.printRecord("№ п/п","Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Результат", "Место", "Отставание")
        groupResults.forEachIndexed { index, (number, _) ->
            val participant = participantByNumber[number]
            require(participant != null) { logger.error { "participant $number is null" } }

            val res = resultByNumber[number]

            val place = if (res == null) null else index + 1 //mustn't be a place if participant doesn't have a result

            val leaderResult = resultByNumber[groupLeaders[groupName]]
            require(leaderResult != null) { logger.error { "$number group leader is absent" }}
            val gap = if (res == null || place == 1) null //mustn't be a gap if participant doesn't have a result or has a first place
            else getGap(leaderResult, res)

            val localTimeGap = if (gap == null) null else (LocalTime.of(0, 0) + gap).format(DateTimeFormatter.ISO_TIME)

            csvPrinter.printRecord(index + 1, number, participant.lastName, participant.firstName,
                participant.yearOfBirth, participant.sportsCategory, participant.team,
                if (res != null) (LocalTime.of(0, 0) + res).format(DateTimeFormatter.ISO_TIME) else  WITHDRAWN,
                place, localTimeGap)
        }*/
    }
}