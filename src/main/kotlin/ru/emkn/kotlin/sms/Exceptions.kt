package ru.emkn.kotlin.sms

open class MyException(message: String) : Exception(message)

class InputFileNotFound(path: String) : MyException("File $path doesn't exist")

class CantReadInputFile(path: String) : MyException("File $path isn't readable")

class CantWriteInOutputFile(path: String) : MyException("$path isn't a writable file")

class IsNotInt(it: String) : MyException("$it must be Int")

class AbsentOfStartFinishRecord(number: Int, record: String) : MyException("$number hasn't $record time")

class FalseStart(number: Int) : MyException("$number false start")

class WrongCheckPoint(number: Int) : MyException("$number wrong check point")

class WrongSplit() : MyException("wrong split")