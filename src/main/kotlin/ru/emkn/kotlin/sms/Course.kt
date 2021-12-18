package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader

data class Course(val name: String, val checkPoints: List<Int>)

val courses = courseParse(makeReader(rawConfig.courses)).toMutableList()

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