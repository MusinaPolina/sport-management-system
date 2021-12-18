package ru.emkn.kotlin.sms

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Reader
import kotlin.math.max
import java.time.Duration

class Group(val name: String, val course: Course, var leaderResult: ParticipantResult? = null) {
    private fun participants() = participants.filter { it.group == this }

    private fun sortedParticipants() = participants().associateWith {
        checkSplit(it, splits.find { split -> split.number == it.number })
    }.toList().sortedBy { (_, split) -> split?.race?.time() ?:  Duration.ofDays(1) }

    private fun checkSplit(participant: Participant, split: Split?): Split? {
        if (split == null) return null
        val race = split.race
        return when (checkStart(participant, race) && checkFinish(race) &&
                checkRace(race) && increasingTimes(race)) {
            true -> split
            else -> null
        }
    }

    private fun checkStart(participant: Participant, race: Race): Boolean {
        if (race.checkPoints.isEmpty()) {
            return false
        }
        val startTime = startTimes.find { it.participant == participant } ?: throw AbsentOfStartTime(participant.number)
        return CheckPoint(config.start, startTime.time) == race.start()
    }

    private fun checkFinish(race: Race): Boolean {
        if (race.checkPoints.isEmpty()) {
            return false
        }
        return config.finish == race.finish().number
    }

    private fun checkRace(race: Race): Boolean {
        val common = longestCommonSubsequence(course.checkPoints, race.checkPoints.map { it.number } )
        return common >= course.points
    }

    private fun increasingTimes(race: Race): Boolean {
        race.checkPoints.dropLast(1).forEachIndexed { index, checkPoint ->
            if (checkPoint.time > race.checkPoints[index + 1].time) return false
        }
        return true
    }

    private fun <T> longestCommonSubsequence (a: List<T>, b: List<T>): Int {
        val n = a.size
        val m = b.size
        val lcsDP = List(n + 1) { MutableList(m + 1) {0} }
        //lcs[x][y] - LCS for a[0..x - 1] and b[0..y - 1]
        for (x in 1..n) {
            for (y in 1..m) {
                if (a[x - 1] == b[y - 1])
                    lcsDP[x][y] = lcsDP[x - 1][y - 1] + 1
                else
                    lcsDP[x][y] = max(lcsDP[x - 1][y], lcsDP[x][y - 1])
            }
        }
        return lcsDP[n][m]
    }

    private fun updateLeader() {
        try {
            val (participant, split) = sortedParticipants().first()
            leaderResult = split?.race?.let { ParticipantResult(it, 1, participant) }
        } catch (e: NoSuchElementException) {
            return
        }
    }

    fun computeResult() {
        updateLeader()
        sortedParticipants().forEachIndexed { place, (participant, split) ->
            results.add(RowResult(participant, split?.race, place + 1))
        }
    }
}

val groups = groupsParse(makeReader(rawConfig.groups))

private fun groupsParse(reader: Reader) : List<Group> {
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim())
    val name = csvParser.headerNames[0]
    val course = csvParser.headerNames[1]
    return csvParser.map { record ->
        Group(record.get(name),
            courses.find { current -> current.name == record.get(course) } ?:
            throw AbsentOfCourse(record.get(name), record.get(course))
        )
    }
}
