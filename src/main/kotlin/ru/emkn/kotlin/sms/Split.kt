package ru.emkn.kotlin.sms

import java.time.Duration
import java.time.LocalTime

data class CheckPoint(val number: Int, val time: LocalTime)

class Race(list: List<CheckPoint>) {
    val start: CheckPoint
    val finish: CheckPoint
    val checkPoints: List<CheckPoint>
    init {
        start = list.first()
        finish = list.last()
        checkPoints = list.dropLast(1).drop(1)
    }
}

class Split(val number: Int, val race: Race?)

val splits = mutableListOf<Split>()
val startTimes = mutableListOf<CheckPoint>()