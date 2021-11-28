package ru.emkn.kotlin.sms

import com.github.ajalt.clikt.core.subcommands
import mu.KotlinLogging
import java.time.Duration
import java.time.LocalTime

val logger = KotlinLogging.logger {}

val participantByNumber = mutableMapOf<Int, Participant>()
val resultByNumber = mutableMapOf<Int, Duration?>()
val groupLeaders = mutableMapOf<String, Int>()

fun main(args: Array<String>) {
    try {
        Command().subcommands(StartTime(), Results(), Teams()).main(args)
    } catch (exc: MyException) {
        println("Error: ${exc.message}")
    }
}
