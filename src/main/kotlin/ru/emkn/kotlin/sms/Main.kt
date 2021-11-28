package ru.emkn.kotlin.sms

import com.github.ajalt.clikt.core.subcommands
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = Command().subcommands(StartTime(), Results(), Teams()).main(args)
