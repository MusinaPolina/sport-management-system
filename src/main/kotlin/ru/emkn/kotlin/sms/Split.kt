package ru.emkn.kotlin.sms

import androidx.compose.runtime.mutableStateListOf
import java.time.Duration
import java.time.LocalTime

data class CheckPoint(val number: Int, val time: LocalTime)

data class ParticipantStartTime(val participant: Participant, val time: LocalTime)

class Race(val checkPoints: List<CheckPoint>) {
    fun start(): CheckPoint {
        return checkPoints.first()
    }
    fun finish(): CheckPoint {
        return checkPoints.last()
    }
    fun time(): Duration {
        return Duration.between(start().time, finish().time)
    }
}

class Split(val number: Int, val race: Race)

val splits = mutableStateListOf<Split>()
val startTimes = mutableStateListOf<ParticipantStartTime>()