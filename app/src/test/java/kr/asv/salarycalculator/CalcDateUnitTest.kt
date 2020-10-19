package kr.asv.salarycalculator

import org.junit.Test
import java.util.*

class CalcDateUnitTest {
    @Test
    fun unitTest(){
        println(getYmd())
    }

    private fun getYmd(): Int{
        return Calendar.getInstance().run {
            val y = get(Calendar.YEAR)
            val m = get(Calendar.MONTH)+1
            val d = get(Calendar.DAY_OF_MONTH)
            y*10000+m*100+d
            //String.format("%d%d%d",y,m,d).toInt()
            //"{$y}{$m}{$d}".toInt()
        }
    }
}