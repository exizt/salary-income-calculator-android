package kr.asv.salarycalculator

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

/**
 * CalcMathUtil 계산 관련 유틸.
 * @author exizt
 * @version 2.1
 * @since 2020-10-18
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
internal object CalcMath {

    /**
     * 절삭 처리.
     *
     * @param v 값 (Long)
     * @param digits 자리수 (Int) : 2 또는 -2 (100의 자리로) / 0 (1의 자리로)
     * @return Double
     */
    fun floor(v: Long, digits: Int): Long{
        var multiplier = 1
        repeat( abs(digits) ) {
            multiplier *= 10
        }
        return (v / multiplier) * multiplier
    }

    /**
     * 절삭 처리.
     *
     * @param v 값 (BigInteger)
     * @param digits 자리수 (Int) : 양수, 0 (아무것도 안 함) / -2 (100의 자리로)
     * @return BigInteger
     */
    fun floor(v: BigInteger, digits: Int): BigInteger {
        var multiplier = 1
        repeat( abs(digits) ) {
            multiplier *= 10
        }
        return (v / multiplier.toBigInteger()) * multiplier.toBigInteger()
        //return round(v, digits, RoundingMode.FLOOR)
    }

    /**
     * 소수점에서 반올림
     *
     * @param v 값 (Double)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @return Double
     */
    fun round(v: Double, digits: Int): Double{
        var multiplier = 1
        repeat( abs(digits) ) {
            multiplier *= 10
        }

        return if (digits > 0){
            // 소수점 이하 처리.
            round(v * multiplier) / multiplier
        } else {
            // 소수점 이상 처리
            round(v / multiplier) * multiplier
        }
    }

    /**
     * 소수점 버림.
     *
     * @param v 값 (Double)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @return Double
     */
    fun roundFloor(v: Double, digits: Int): Double{
        var multiplier = 1
        repeat( abs(digits) ) {
            multiplier *= 10
        }

        return if (digits > 0){
            // 소수점 이하 처리.
            floor(v * multiplier) / multiplier
        } else {
            // 소수점 이상 처리
            floor(v / multiplier) * multiplier
        }
    }

    /**
     * 소수점 반올림 (BigDecimal).
     *
     * 구현) 그냥 1 로 나눈 뒤 rounding 을 해준다.
     * setScale 메서드를 활용하는 방법이 원래 맞는데, 이거 가끔 버그가 있으므로 divide 를 해주면서 처리하자.
     *
     * @param v 값 (BigDecimal)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @return BigDecimal
     */
    fun round(v: BigDecimal, digits: Int, mode: RoundingMode = RoundingMode.HALF_UP): BigDecimal {
        return v.divide(BigDecimal.ONE,digits,mode)
    }

    /**
     * 소수점 버림 (BigDecimal).
     *
     * @param v 값 (BigDecimal)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @return BigDecimal
     */
    fun roundFloor(v: BigDecimal, digits: Int): BigDecimal {
        return round(v, digits, RoundingMode.FLOOR)
    }

    /**
     * 반올림
     * 정수형이어서 정수 단위에서 반올림할 때 사용할 수 있음. 근데 의미가 있는지 모르겠다...
     *
     * @param v 값 (BigInteger)
     * @param digits 자리수 (Int) : 양수, 0 (아무것도 안 함) / -2 (100의 자리로)
     * @return BigInteger
     */
    fun round(v: BigInteger, digits: Int, mode: RoundingMode = RoundingMode.HALF_UP): BigInteger{
        val tDigits = if(digits > 0) 0 else digits
        return round(v.toBigDecimal(), tDigits, mode).toBigInteger()
    }

    /**
     * 가끔 비교식이 안 맞을 때가 있으므로 이걸 사용.
     * BigDecimal.equals 메서드는 이슈가 있다. (12340 != 1.234E+4 의 이슈)
     */
    fun equals(v: BigDecimal, v2:BigDecimal): Boolean{
        return v.compareTo(v2) == 0
    }

    /**
     * BigDecimal/BigDecimal 나눗셈
     *
     * @param dividend 원값 (BigDecimal)
     * @param divisor 나누는 값 (BigDecimal)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @param mode RoundingMode : 기본값 HALF_UP
     * @return BigDecimal
     */
    fun divide(dividend: BigDecimal, divisor: BigDecimal, digits: Int, mode: RoundingMode = RoundingMode.HALF_UP): BigDecimal {
        return dividend.divide(divisor, digits, mode)
    }

    /**
     * BigDecimal/Int 나눗셈
     *
     * @param dividend 원값 (BigDecimal)
     * @param divisor 나누는 값 (Int)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @param mode RoundingMode : 기본값 HALF_UP
     * @return BigDecimal
     */
    fun divide(dividend: BigDecimal, divisor: Int, digits: Int, mode: RoundingMode = RoundingMode.HALF_UP): BigDecimal {
        return divide(dividend, divisor.toBigDecimal(), digits, mode)
    }

    /**
     * BigInteger/Int 나눗셈
     *
     * @param dividend 원값 (BigInteger)
     * @param divisor 나누는 값 (Int)
     * @param digits 자리수 (Int) : 2 (소수점 2자리로) / 0 (1의 자리로) / -2 (100의 자리로)
     * @param mode RoundingMode : 기본값 HALF_UP
     * @return BigDecimal
     */
    fun divide(dividend: BigInteger, divisor: Int, digits: Int, mode: RoundingMode = RoundingMode.HALF_UP): BigDecimal {
        return divide(dividend.toBigDecimal(), divisor.toBigDecimal(), digits, mode)
    }

    fun multiply(a:Long, b:Double): Long{
        return (a.toBigDecimal() * b.toBigDecimal()).toLong()
    }
}