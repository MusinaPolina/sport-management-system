package ru.emkn.kotlin.sms

import mu.KotlinLogging
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.*

val logger = KotlinLogging.logger {}

fun makeReader(pathStr: String) : Reader {
    val path = Paths.get(pathStr)
    if (!path.exists() || !path.isRegularFile()) {
        logger.error { "file $pathStr doesn't exists" }
        throw InputFileNotFound(pathStr)
    }
    if (!path.isReadable()) {
        logger.error { "file $pathStr isn't readable" }
        throw CantReadInputFile(pathStr)
    }
    return Files.newBufferedReader(path)
}

fun makeWriter(pathStr: String) : Writer {
    val path = Paths.get(pathStr)
    if (!path.exists())
        path.createFile()
    if (!path.isWritable() || !path.isRegularFile()) {
        logger.error { "file $pathStr isn't writable" }
        throw CantWriteInOutputFile(pathStr)
    }
    return Files.newBufferedWriter(path)
}

fun makeStandardWriter() = BufferedWriter(OutputStreamWriter(System.out))

fun main(args: Array<String>) {
    TODO()
}
