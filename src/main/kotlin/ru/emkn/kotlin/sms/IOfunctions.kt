package ru.emkn.kotlin.sms

import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.*

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

fun getOpenLink(): String? {
    val fileOpen = JFileChooser(".")
    val filterCsv = FileNameExtensionFilter("CSV files (*.csv)", "csv")
    fileOpen.fileFilter = filterCsv
    fileOpen.fileSelectionMode = JFileChooser.FILES_ONLY
    val getFile = fileOpen.showOpenDialog(null)
    if (getFile == JFileChooser.APPROVE_OPTION) {
        return fileOpen.selectedFile.toString()
    }
    return null
}

fun getOpenLinkMultiple(): List<String> {
    val fileOpen = JFileChooser(".")
    val filterCsv = FileNameExtensionFilter("CSV files (*.csv)", "csv")
    fileOpen.fileFilter = filterCsv
    fileOpen.fileSelectionMode = JFileChooser.FILES_ONLY
    fileOpen.isMultiSelectionEnabled = true
    val getFile = fileOpen.showOpenDialog(null)
    if (getFile == JFileChooser.APPROVE_OPTION) {
        return fileOpen.selectedFiles.map {it.toString()}
    }
    return listOf()
}

fun getSaveLink(): String? {
    val fileOpen = JFileChooser(".")
    val filterCsv = FileNameExtensionFilter("CSV files (*.csv)", "csv")
    fileOpen.fileFilter = filterCsv
    fileOpen.fileSelectionMode = JFileChooser.FILES_ONLY
    val getFile = fileOpen.showSaveDialog(null)
    if (getFile == JFileChooser.APPROVE_OPTION) {
        return fileOpen.selectedFile.toString()
    }
    return null
}