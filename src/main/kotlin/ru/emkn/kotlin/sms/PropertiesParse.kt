package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import java.nio.file.Paths

data class RawConfig(
    val start: Int,
    val finish: Int,
    val event: String,
    val groups: String,
    val courses: String,
)

private val rawConfig = ConfigLoader()
    .loadConfigOrThrow<RawConfig>(Paths.get("src/main/resources/main.properties"))

data class Config(
    val start: Int,
    val finish: Int,
    val event: Reader,
    val courses: List<Course>,
    val groups: List<Group>,
)

val config = Config(
    rawConfig.start,
    rawConfig.finish,
    makeReader(rawConfig.event),
    courseParse(makeReader(rawConfig.courses)),
    groupsParse(makeReader(rawConfig.groups)),
)

private fun groupsParse(reader: Reader) : List<Group> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames[0]
    val course = csvParser.headerNames[1]
    return csvParser.map { record ->
        Group(record.get(name),
            config.courses.find { current -> current.name == record.get(course) } ?:
            throw AbsentOfCourse(record.get(name), record.get(course))
        )
    }
}

private fun courseParse(reader: Reader) : List<Course> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames.first()
    return csvParser.map { Course(
        it.get(name),
        it.toList().drop(1).filter { it1 -> it1 != "" }.map { it1 ->
            it1.toInt()
        })
    }
}