package ru.emkn.kotlin.sms
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ParticipantResult {
    val time: Duration
    val place: Int
    val delay: Duration?
    constructor(time: Duration, place: Int, delay: Duration?) {
        this.time = time
        this.place = place
        this.delay = delay
    }
    constructor(race: Race, place: Int, participant: Participant) {
        this.time = race.time()
        this.place = place
        this.delay = when (place) {
            1 -> null
            else -> Duration.between(
                    participant.group.leaderResult?.time?.let { LocalTime.of(0, 0, it.toSeconds().toInt()) } ?: run {
                        logger.error { "GroupLeader is absent"}
                        throw AbsentOfGroupLeader(participant.group.name)
                    },
                    LocalTime.of(0, 0, 0) + Duration.ofSeconds(race.time().toSeconds()))
        }
    }

    private fun durationToString(duration: Duration?): String {
        return when (duration) {
            null -> ""
            else -> (LocalTime.of(0, 0) + duration).format(DateTimeFormatter.ISO_TIME)
        }
    }

    fun toList(): List<String> {
        return listOf(durationToString(time), place.toString(), durationToString(delay))
    }
}

class RowResult {
    val participant: Participant
    val result: ParticipantResult? // null result - withdrawn

    constructor(participant: Participant, result: ParticipantResult?)  {
        this.participant = participant
        this.result = result
    }

    constructor(participant: Participant, race: Race?, place: Int)  {
        this.participant = participant
        this.result = race?.let { ParticipantResult(it, place, participant) }
    }

    fun toList(): List<String> {
        return participant.toList() + when (result) {
            null -> listOf(WITHDRAWN)
            else -> result.toList()
        }
    }
}

class ComparatorRowResult: Comparator<RowResult>{
    override fun compare(row1: RowResult, row2: RowResult): Int {
        return when {
            row2.result == null -> -1
            row1.result == null -> 1
            else -> row1.result.time.compareTo(row2.result.time)
        }
    }
}

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

    private fun addGroupResults(group: Group, csvPrinter: CSVPrinter) {
        val groupResults = results.filter { it.participant.group == group }.sortedWith(ComparatorRowResult())
        if (groupResults.isEmpty()) {
            return
        }
        csvPrinter.printRecord(group.name)
        csvPrinter.printRecord("№ п/п","Номер", "Фамилия", "Имя", "Г.р.", "Разр", "Команда", "Результат", "Место", "Отставание")
        printGroupResults(groupResults, csvPrinter)
    }

    private fun printGroupResults(groupResults: List<RowResult>, csvPrinter: CSVPrinter) {
        groupResults.forEachIndexed { index, rowResult ->
            csvPrinter.printRecord(listOf(index + 1) + rowResult.toList())
        }
    }
}