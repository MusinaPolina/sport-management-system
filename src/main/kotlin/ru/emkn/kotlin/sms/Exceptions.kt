package ru.emkn.kotlin.sms

open class MyException(message: String) : Exception(message)

class InputFileNotFound(path: String) : MyException("File $path doesn't exist")

class CantReadInputFile(path: String) : MyException("File $path isn't readable")

class CantWriteInOutputFile(path: String) : MyException("$path isn't a writable file")

class IsNotInt(number: String) : MyException("record number $number is not Int")

class AbsentOfStartFinishRecord(number: Int, record: String) : MyException("$number hasn't $record time")
