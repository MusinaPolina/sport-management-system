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
    val courseByGroup: Map<String, String>,
    val courseCheckPoints: Map<String, List<Int>>,
)

val config = Config(
    rawConfig.start,
    rawConfig.finish,
    makeReader(rawConfig.event),
    groupsParse(makeReader(rawConfig.groups)),
    courseParse(makeReader(rawConfig.courses)),
)

private fun groupsParse(reader: Reader) : Map<String, String> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames[0]
    val course = csvParser.headerNames[1]
    return csvParser.associate {
        it.get(name) to it.get(course)
    }
}

private fun courseParse(reader: Reader) : Map<String, List<Int>> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames.first()
    return csvParser.associate {
        it.get(name) to it.toList().drop(1).filter { it1 -> it1 != "" }.map { it1 ->
            it1.toInt()
        }
    }
}