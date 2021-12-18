package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.time.Duration

class Group(val name: String, val course: Course, var leaderResult: ParticipantResult? = null) {
    private fun participants() = participants.filter { it.group == this }

    private fun sortedParticipants() = participants().associateWith {
        splits.find { split -> split.number == it.number
        }}.toList().sortedBy { (_, split) ->
        split?.let { it.race?.time() ?: Duration.ofDays(1)} ?:  Duration.ofDays(1) }

    private fun updateLeader() {
        try {
            val (participant, split) = sortedParticipants().first()
            leaderResult = split?.race?.let { ParticipantResult(it, 1, participant) }
        } catch (e: NoSuchElementException) {
            return
        }
    }

    fun computeResult() {
        updateLeader()
        sortedParticipants().forEachIndexed { place, (participant, split) ->
            results.add(RowResult(participant, split?.race, place))
        }
    }
}

val groups = groupsParse(makeReader(rawConfig.groups)).toMutableList()

private fun groupsParse(reader: Reader) : List<Group> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames[0]
    val course = csvParser.headerNames[1]
    return csvParser.map { record ->
        Group(record.get(name),
            courses.find { current -> current.name == record.get(course) } ?:
            throw AbsentOfCourse(record.get(name), record.get(course))
        )
    }
}
