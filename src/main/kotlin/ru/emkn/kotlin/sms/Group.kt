package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader

data class Group(val name: String, val course: Course, var leaderResult: ParticipantResult? = null)

fun getCourse(groupName: String): Course {
    return groups.find { group -> group.name == groupName }?.course ?: throw AbsentOfGroup(groupName)
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
