import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.Duration
import kotlin.test.*

class TestResults {


    @Test
    fun checkParseInput() {
        val startTimeFile = File.createTempFile("start", ".csv")
        val splitFile = File.createTempFile("split", ".csv")
        startTimeFile.writeText("""
            М10
            Номер,Фамилия,Имя,Г.р.,Разр,Команда,Время старта
            100,НИКИТИНА,АЛЛА,1939,,"ПСКОВ,РУСЬ",12:00:00
            101,Иванов,Иван,1930,,"ПСКОВ,РУСЬ",12:00:01
        """.trimIndent())
        splitFile.writeText("""
            100,241,12:00:00,32,12:00:01,46,12:00:02,34,12:00:03,33,12:00:04,53,12:00:05,240,12:00:50
            101,241,12:00:01,32,12:01:01,46,12:01:02,34,12:01:03,33,12:01:04,53,12:01:05,240,12:01:11
        """.trimIndent())
        val resultFile = File.createTempFile("result", ".csv")
        val resultWriter = resultFile.bufferedWriter()
        results(startTimeFile.reader(), splitFile.reader(), resultWriter)
        val text = resultFile.readText()
        println(text)
        /*val applicationFile = File.createTempFile("tmp", ".csv")
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
        assertEquals(mapOf("М10" to 232), groupLeaders)*/
    }

}