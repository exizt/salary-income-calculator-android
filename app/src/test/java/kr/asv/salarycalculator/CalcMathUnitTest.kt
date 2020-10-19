package kr.asv.salarycalculator

import junit.framework.Assert.assertEquals
import junit.framework.ComparisonFailure
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode


class CalcMathUnitTest {

    @Test
    fun doubleTest(){
        val a: Long = 95002400
        val b: BigDecimal = a.toBigDecimal() * 0.35.toBigDecimal()

        println(b.toLong())
    }

    @Test
    fun unitTest(){

        /**
         * 반올림 테스트
         */
        // test 1
        val r1 = CalcMath.round(12345.12345678, 4)
        assertEquals(12345.1235, r1)
        // test 2
        assertEquals(12350.0, CalcMath.round(12345.12345678, -1))

        /**
         * 버림 테스트
         */
        // test 1
        val rf1 = CalcMath.roundFloor(12345.12345678, 4)
        assertEquals(12345.1234, rf1)
        // test 2
        assertEquals(12340.0, CalcMath.roundFloor(12345.12345678, -1))
    }

    @Test
    fun bigDecimalRoundAdvancedTest(){
        /**
         * 반올림 테스트
         */
        // test 1
        val r1 = CalcMath.round(BigDecimal("12345.12345678"), 4)
        assertEquals(BigDecimal("12345.1235"), r1)
        // test 2
        val r2 = CalcMath.round(BigDecimal("12345.12345678"), -1)
        //assertTrue(CalcUtil.equals(BigDecimal("12350"), r2))
        assertEqualsBigDecimal("12350", r2)

        /**
         * 버림 테스트
         */
        // test 1
        val rf1 = CalcMath.roundFloor(BigDecimal("12345.12345678"), 4)
        assertEqualsBigDecimal("12345.1234", rf1)
        // test 2
        val rf2 = CalcMath.roundFloor(BigDecimal("12345.12345678"), -1)
        assertEqualsBigDecimal("12340", rf2)
    }

    @Test
    fun bigDecimalRoundTest(){

        /**
         * 반올림 테스트
         */
        // test 1
        val r1 = CalcMath.round(BigDecimal("12345.12345678"), 4)
        assertEqualsBigDecimal("12345.1235", r1)
        // test 2
        val r2 = CalcMath.round(BigDecimal("12345.12345678"), -1)
        assertEqualsBigDecimal("12350", r2)

        /**
         * 버림 테스트
         */
        // test 1
        val rf1 = CalcMath.roundFloor(BigDecimal("12345.12345678"), 4)
        assertEqualsBigDecimal("12345.1234", rf1)
        // test 2
        val rf2 = CalcMath.roundFloor(BigDecimal("12345.12345678"), -1)
        assertEqualsBigDecimal("12340", rf2)
    }

    @Test
    fun bigDecimalTest(){
        println(BigDecimal("12345.12345678"))
        println(BigDecimal("12345000"))
        println(BigDecimal("12345.12345678").setScale(-1, RoundingMode.FLOOR))
        println(BigDecimal("12345.12345678").divide(BigDecimal.ONE, -1, RoundingMode.FLOOR))

        println(BigInteger("12345000"))
        println(BigInteger("12345000").toDouble())
    }

    @Test
    fun bigDecimalDivideTest(){
        /**
         * 나눗셈 테스트
         */
        // test 1
        val v1 = BigDecimal("12345.12345678")
        assertEqualsBigDecimal("1234.5123", v1.divide(BigDecimal("10"), 4, RoundingMode.HALF_UP))
        /**
         * 반올림 테스트
         */
        // test 1
        val vr1 = BigDecimal("12345.12345678")
        assertEqualsBigDecimal("12345.1235", vr1.divide(BigDecimal("1"), 4, RoundingMode.HALF_UP))
        // test 1 - 2
        assertEqualsBigDecimal("12345.1235", vr1.setScale(4, RoundingMode.HALF_UP))
        // test 2
        val vr2 = BigDecimal("12345.12345678").divide(BigDecimal("1"), -1, RoundingMode.HALF_UP)
        assertEqualsBigDecimal("12350", vr2)
    }

    /**
     * BigDecimal 을 비교하기에 기존 메서드가 좀 부족해서 튜닝함.
     * 원본은 junit.framework.Assert 의 assertEquals 메서드.
     */
    private fun assertEqualsBigDecimal(expected: String, actual: BigDecimal){
        if (BigDecimal(expected).compareTo(actual) == 0)
            return

        throw ComparisonFailure(null, expected, actual.toString())
    }
}