package kr.asv.salarycalculator.calculator

import kotlin.math.max
import kotlin.math.min

class Insurance {

    /**
     * 4대보험요율
     */
    val rates = InsuranceRates()

    /**
     * 국민연금
     */
    var nationalPension: Long = 0
        set(value) {
            field = max(value, 0) // 음수 방지
        }

    /**
     * 건강보험
     */
    var healthCare: Long = 0
        set(value) {
            field = max(value, 0) // 음수 방지
        }

    /**
     * 장기요양보험
     */
    var longTermCare: Long = 0
        set(value) {
            field = max(value, 0) // 음수 방지
        }

    /**
     * 고용보험
     */
    var employmentCare: Long = 0
        set(value) {
            field = max(value, 0) // 음수 방지
        }

    // 국민연금 상한, 하한액
    var limitNpUp: Long = 5030000
    var limitNpDown: Long = 320000

    // 건강보험 상한, 하한액
    var limitHcUp: Long = 3182760
    var limitHcDown: Long = 18600

    /**
     * 4대 보험 계산
     * 각각의 보험요율을 계산한다.
     */
    fun calculate(adjustedSalary: Long) {
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
    private fun computeNationalPension(_adjustedSalary: Long): Long {
        var adjustedSalary = _adjustedSalary

        // 상한,하한 보정
        adjustedSalary = max(adjustedSalary, limitNpDown) // 하한 기준 보정
        adjustedSalary = min(adjustedSalary, limitNpUp) // 상한 기준 보정

        // 소득월액 천원미만 절사 (백단위 절사)
        //adjustedSalary = floor(adjustedSalary / 1000) * 1000
        adjustedSalary = CalcMath.floor(adjustedSalary, 3)

        // 연산식
        val result = N(adjustedSalary) * rates.nationalPension

        // 보험료값 십원 미만 절사 (원단위 절사)
        //nationalPension = floor(result / 10) * 10
        return CalcMath.floor(result.toLong(), 1)
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private fun computeHealthCare(adjustedSalary: Long): Long {
        val calc = N(adjustedSalary) * rates.healthCare

        // 십원 미만 절사.
        //result = floor(result / 10) * 10
        var result = CalcMath.floor(calc.toLong(), 1)

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
    private fun computeLongTermCare(healthCare: Long): Long {
        val result = N(healthCare) * rates.longTermCare

        // 십원 미만 절사 (원단위 절삭)
        //longTermCare = floor(result / 10) * 10
        return CalcMath.floor(result.toLong(), 1)
    }

    /**
     * 고용보험 계산식
     */
    private fun computeEmploymentCare(adjustedSalary: Long): Long {
        val result = N(adjustedSalary) * rates.employmentCare

        // 십원 미만 절사 (원단위 절삭)
        //employmentCare = floor(result / 10) * 10
        return CalcMath.floor(result.toLong(), 1)
    }

    /**
     * 4대보험 합산 금액 리턴
     */
    fun get(): Long {
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