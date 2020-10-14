package kr.asv.salarycalculator

import org.junit.Test
import kotlin.math.floor

class SalaryIntervalUnitTest {
    @Test
    fun unitTest(){
        val num = 1248000.0
        val a = a(num)
        println("a [$a]")

        val b = b(num)
        println("b [$b]")

        val c = c(num)
        println("c [$c]")
    }

    @Suppress("SameParameterValue")
    private fun a(value: Double) : Double {
        return if (value % 10000 > 5000) {
            floor(value / 10000) * 10000 + 7500
        } else {
            floor(value / 10000) * 10000 + 2500
        }
    }

    @Suppress("SameParameterValue")
    private fun b(value: Double) : Double {
        return floor(value / 5000) * 5000 + 2500
    }

    @Suppress("SameParameterValue")
    private fun c(value: Double) : Double {
        return getIntervalMedium(value, 5000)
    }

    @Suppress("SameParameterValue")
    private fun getIntervalMedium(value: Double, interval : Int) : Double {
        return floor(value / interval) * interval + (interval / 2)
    }
}