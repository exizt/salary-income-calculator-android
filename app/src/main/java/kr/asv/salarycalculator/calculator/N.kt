package kr.asv.salarycalculator.calculator

import java.math.BigDecimal
import java.math.BigInteger

/**
 * CalcMathUtil 계산 관련 유틸.
 * @author exizt
 * @version 2.1
 * @since 2020-10-18
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
internal class N {
    var a : BigDecimal
    constructor(v:BigDecimal){
        a = v
    }
    constructor(v: Long){
        a = v.toBigDecimal()
    }
    constructor(v: Double){
        a = v.toBigDecimal()
    }
    operator fun plus(other: N): N {
        return N(a + other.toBigDecimal())
    }
    operator fun plus(other:Long): N {
        return N(a + other.toBigDecimal())
    }
    operator fun minus(other: N): N {
        return N(a - other.toBigDecimal())
    }
    operator fun minus(other:Long): N {
        return N(a - other.toBigDecimal())
    }
    operator fun times(other: N): N {
        return N(a * other.toBigDecimal())
    }
    operator fun times(other: Double): N {
        return N(a * other.toBigDecimal())
    }
    operator fun div(other: N): N {
        return N(a / other.toBigDecimal())
    }
    operator fun div(other: Int): N {
        return N(a / other.toBigDecimal())
    }
    fun toBigDecimal(): BigDecimal{
        return a
    }
    fun toBigInteger(): BigInteger{
        return a.toBigInteger()
    }
    fun toLong():Long{
        return a.toLong()
    }
}