package ru.emkn.kotlin.sms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option

class Command : CliktCommand() {
    override fun run() = Unit
}

class StartTime : CliktCommand() {
    private val output by option("-o", "--output", help="output file")
    private val applications by argument().multiple()
    override fun run() {
        applicationsToStart(
            applications.map { makeReader(it) },
            output?.let { makeWriter(it) } ?: makeStandardWriter()
        )
    }
}

class Results : CliktCommand() {
    private val output by option("-o", "--output", help="output file")
    private val start by argument()
    private val splits by argument()
    override fun run() {
        TODO("Not yet implemented")
    }
}

class Teams : CliktCommand() {
    private val output by option("-o", "--output", help="output file")
    private val results by argument()
    override fun run() {
        teamResults(
            makeReader(results),
            output?.let { makeWriter(it) } ?: makeStandardWriter()
        )
    }
}