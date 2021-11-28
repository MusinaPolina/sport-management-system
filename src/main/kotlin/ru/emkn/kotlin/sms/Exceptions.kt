package ru.emkn.kotlin.sms

open class MyException(message: String) : Exception(message)

class InputFileNotFound(path: String) : MyException("File $path doesn't exist")

class CantReadInputFile(path: String) : MyException("File $path isn't readable")

class CantWriteInOutputFile(path: String) : MyException("$path isn't a writable file")