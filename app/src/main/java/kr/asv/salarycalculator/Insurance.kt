package kr.asv.salarycalculator

import kotlin.math.max
import kotlin.math.min

class Insurance {
    /**
     * 국민연금
     */
    var nationalPension = 0.0

    /**
     * 건강보험
     */
    var healthCare = 0.0

    /**
     * 장기요양보험
     */
    var longTermCare = 0.0

    /**
     * 고용보험
     */
    var employmentCare = 0.0

    /**
     * 4대보험요율
     */
    val rates = InsuranceRates()

    // 국민연금 상한, 하한액
    var limitNpUp = 5030000.toDouble()
    var limitNpDown = 320000.toDouble()

    // 건강보험 상한, 하한액
    var limitHcUp = 3182760.toDouble()
    var limitHcDown = 18600.toDouble()

    /**
     * 4대 보험 계산
     * 각각의 보험요율을 계산한다.
     */
    fun calculate(adjustedSalary: Double) {
        nationalPension = computeNationalPension(adjustedSalary) // 국민연금
        healthCare = computeHealthCare(adjustedSalary) // 건강보험
        longTermCare = computeLongTermCare(healthCare) // 요양보험
        employmentCare = computeEmploymentCare(adjustedSalary) // 고용보험
    }

    /**
     * 국민연금 계산식
     *
     * @param _adjustedSalary Double 세금의 기준 봉급액(기본급 - 비과세)
     */
    fun computeNationalPension(_adjustedSalary: Double): Double {
        var adjustedSalary = _adjustedSalary

        // 상한,하한 보정
        adjustedSalary = max(adjustedSalary, limitNpDown) // 하한 기준 보정
        adjustedSalary = min(adjustedSalary, limitNpUp) // 상한 기준 보정

        // 소득월액 천원미만 절사 (백단위 절사)
        //adjustedSalary = floor(adjustedSalary / 1000) * 1000
        adjustedSalary = CalcMath.roundFloor(adjustedSalary, -3)

        // 연산식
        val result = adjustedSalary * rates.nationalPension

        // 보험료값 십원 미만 절사 (원단위 절사)
        //nationalPension = floor(result / 10) * 10
        return CalcMath.roundFloor(result, -1)
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private fun computeHealthCare(adjustedSalary: Double):Double {
        var result = adjustedSalary * rates.healthCare

        // 십원 미만 절사.
        //result = floor(result / 10) * 10
        result = CalcMath.roundFloor(result, -1)

        // 보험료액의 상한 기준 보정
        result = max(result, limitHcDown)
        result = min(result, limitHcUp)

        // 보험료값 십원 미만 절사 (원단위 절사)
        //healthCare = floor(result / 10) * 10
        //healthCare = CalcMath.roundFloor(result, -1)
        return result
    }

    /**
     * 장기요양보험 계산식
     *
     */
    private fun computeLongTermCare(healthCare: Double): Double {
        val result = healthCare * rates.longTermCare

        // 십원 미만 절사 (원단위 절삭)
        //longTermCare = floor(result / 10) * 10
        return CalcMath.roundFloor(result, -1)
    }

    /**
     * 고용보험 계산식
     */
    private fun computeEmploymentCare(adjustedSalary: Double): Double {
        val result = adjustedSalary * rates.employmentCare

        // 십원 미만 절사 (원단위 절삭)
        //employmentCare = floor(result / 10) * 10
        return CalcMath.round(result, -1)
    }

    /**
     * 4대보험 합산 금액 리턴
     */
    fun get(): Double {
        return nationalPension + healthCare + longTermCare + employmentCare
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        return """
            <4대보험 연산 클래스>
            국민연금 :   $nationalPension
            건강보험 :   $healthCare
            장기요양 :   $longTermCare
            고용보험료 : $employmentCare
        """.trimIndent()
    }
}