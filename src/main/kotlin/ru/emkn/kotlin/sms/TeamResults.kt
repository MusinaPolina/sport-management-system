package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths

fun teamResults(link: String = "sample-data/results.csv") {
    val reader = Files.newBufferedReader(Paths.get(link))
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())
}