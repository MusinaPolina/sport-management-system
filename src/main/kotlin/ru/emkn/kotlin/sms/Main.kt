package ru.emkn.kotlin.sms

import com.github.ajalt.clikt.core.subcommands
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    try {
        Command().subcommands(StartTime(), Results(), TeamsResults()).main(args)
    } catch (exc: MyException) {
        println("Error: ${exc.message}")
    }
}
