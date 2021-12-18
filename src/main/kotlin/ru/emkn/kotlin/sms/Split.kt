package ru.emkn.kotlin.sms

import java.time.Duration
import java.time.LocalTime

data class CheckPoint(val number: Int, val time: LocalTime)

data class ParticipantStartTime(val participant: Participant, val time: LocalTime)

class Race(list: List<CheckPoint>) {
    val start: CheckPoint
    val finish: CheckPoint
    val checkPoints: List<CheckPoint>
    init {
        start = list.first()
        finish = list.last()
        checkPoints = list.dropLast(1).drop(1)
    }
    fun time(): Duration {
        return Duration.between(start.time, finish.time)
    }
}

class Split(val number: Int, val race: Race?)

val splits = mutableListOf<Split>()
val startTimes = mutableListOf<ParticipantStartTime>()