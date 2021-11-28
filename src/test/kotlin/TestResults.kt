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
        assertEquals("""
            Протокол результатов.
            М10
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            1,100,НИКИТИНА,АЛЛА,1939,,"ПСКОВ,РУСЬ",00:00:50,1,
            2,101,Иванов,Иван,1930,,"ПСКОВ,РУСЬ",00:01:10,2,00:00:20
            М12
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М14
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М16
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М18
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М21
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М40
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            М60
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж10
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж12
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж14
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж16
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж18
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж21
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж40
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Ж60
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Мстуд
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            Жстуд
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
            VIP
            № п/п,Номер,Фамилия,Имя,Г.р.,Разр,Команда,Результат,Место,Отставание
        """.trimIndent().replace("\n", "\r\n") + "\r\n",
            text)
    }

}