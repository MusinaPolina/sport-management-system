import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.Duration
import kotlin.test.*

class TestTeamResults {

    @Test
    fun checkComputePoints() {
        assertEquals(0, computePoints(null, Duration.ofSeconds(100)))
        assertEquals(0, computePoints(Duration.ofSeconds(120), Duration.ofSeconds(50)))
        assertEquals(50, computePoints(Duration.ofSeconds(150), Duration.ofSeconds(100)))
    }

    @Test
    fun checkParseInput() {
        val applicationFile = File.createTempFile("tmp", ".csv")
        applicationFile.writeText("""
            Протокол результатов.,,,,,,,,,
            М10,,,,,,,,,
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание
            1,232,Никитин,ВИКТОР,2014,,"ПСКОВ,РУСЬ",00:06:58,1,
            2,281,СИМОНОВ,ТИМОФЕЙ,2011,,ШКОЛА №3,00:07:04,2,+0:06
            3,305,ПОВЕТКИН,ВЛАД,0,,ПСКОВ,cнят,,
        """.trimIndent())
        parseInput(applicationFile.reader())
        val participants = listOf(
            Participant("ВИКТОР","Никитин", 2014,"","М10", "ПСКОВ,РУСЬ"),
            Participant("ВЛАД","ПОВЕТКИН", 0, "", "М10", "ПСКОВ"),
            Participant("ТИМОФЕЙ", "СИМОНОВ", 2011, "", "М10", "ШКОЛА №3"))
        assertEquals(mapOf(232 to participants[0], 305 to participants[1], 281 to participants[2]), participantByNumber)
        assertEquals(mapOf(232 to Duration.ofSeconds(418), 305 to null, 281 to Duration.ofSeconds(424)), resultByNumber)
        assertEquals(mapOf("М10" to 232), groupLeaders)
    }

    @Test
    fun check() {

    }
}