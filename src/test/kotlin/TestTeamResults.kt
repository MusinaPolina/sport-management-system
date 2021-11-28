import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.Duration
import kotlin.test.*

class TestTeamResults {

    @Test
    fun check() {
        val resultsFile = File.createTempFile("tmp", ".csv")
        resultsFile.writeText("""
            Протокол результатов.,,,,,,,,,
            М10,,,,,,,,,
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание
            1,232,Никитин,ВИКТОР,2014,,"ПСКОВ,РУСЬ",00:06:58,1,
            2,281,СИМОНОВ,ТИМОФЕЙ,2011,,ШКОЛА №3,00:07:04,2,+0:06
            3,305,ПОВЕТКИН,ВЛАД,0,,ПСКОВ,cнят,,
        """.trimIndent())
        val teams = File.createTempFile("teams", ".csv")
        teamResults(resultsFile.reader(), teams.writer())
        assertEquals("""
            Место,Команда,Результат
            1,"ПСКОВ,РУСЬ",100
            2,ШКОЛА №3,99
            3,ПСКОВ,0
        """.trimIndent().replace("\n","\r\n") + "\r\n",
            teams.readText()
        )
    }
}