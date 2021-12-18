package ru.emkn.kotlin.sms

open class MyException(message: String) : Exception(message)

class InputFileNotFound(path: String) : MyException("File $path doesn't exist")

class CantReadInputFile(path: String) : MyException("File $path isn't readable")

class AbsentOfCourse(group: String, course: String) : MyException("course: $course for group: $group is not declared")

class AbsentOfGroup(group: String) : MyException("group: $group is not declared")

class CantWriteInOutputFile(path: String) : MyException("$path isn't a writable file")

class IsNotInt(it: String) : MyException("$it must be Int")

class IsNotLocalTime(it: String) : MyException("$it must be LocalTime")

//class AbsentOfStartFinishRecord(number: Int, record: String) : MyException("$number hasn't $record time")

class FalseStart(number: Int) : MyException("$number false start")

//class WrongCheckPoint(number: Int) : MyException("$number wrong check point")

class WrongTime : MyException("Time is wrong")

class WrongSplit : MyException("wrong split")

class WrongApplication(team: String, line: Long) :
    MyException("Team $team submitted an incorrect application, line $line")
