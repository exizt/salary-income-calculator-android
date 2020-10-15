package kr.asv.salarycalculator

import kotlin.math.floor
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

    /**
     * 4대 보험 계산
     * 각각의 보험요율을 계산한다.
     */
    fun execute(adjustedSalary: Double) {
        calculateNationalPension(adjustedSalary) // 국민연금
        calculateHealthCareWithLongTermCare(adjustedSalary) // 건강보험 과 요양보험
        calculateEmploymentCare(adjustedSalary) // 고용보험
    }

    /**
     * 국민연금 계산식
     *
     * @param _adjustedSalary Double 세금의 기준 봉급액(기본급 - 비과세)
     */
    private fun calculateNationalPension(_adjustedSalary: Double) {
        var adjustedSalary = _adjustedSalary

        // 상한 하한 보정
        adjustedSalary = max(adjustedSalary, NP_DOWN_LIMIT) // 하한 기준 보정
        adjustedSalary = min(adjustedSalary, NP_UP_LIMIT) // 상한 기준 보정

        // 소득월액 천원미만 절사
        adjustedSalary = floor(adjustedSalary / 1000) * 1000

        // 연산식
        val result = adjustedSalary * rates.nationalPension

        // 보험료값 원단위 절사
        nationalPension = floor(result / 10) * 10
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private fun calculateHealthCareWithLongTermCare(adjustedSalary: Double) {
        calculateHealthCare(adjustedSalary)
        calculateLongTermCare(healthCare)
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private fun calculateHealthCare(adjustedSalary: Double) {
        var result = adjustedSalary * rates.healthCare
        result = floor(result / 10) * 10

        // 상한액
        if (result >= HC_UP_LIMIT) {
            result = HC_UP_LIMIT
        }

        // 보험료값 원단위 절사
        healthCare = floor(result / 10) * 10
    }

    /**
     * 장기요양보험 계산식
     *
     */
    private fun calculateLongTermCare(healthCare: Double) {
        val result = healthCare * rates.longTermCare
        // 원단위 절삭
        longTermCare = floor(result / 10) * 10
    }

    /**
     * 고용보험 계산식
     */
    private fun calculateEmploymentCare(adjustedSalary: Double) {
        val result = adjustedSalary * rates.employmentCare
        // 원단위 절삭
        employmentCare = floor(result / 10) * 10
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

    companion object {
        // 국민연금 상한, 하한액
        private const val NP_DOWN_LIMIT = 300000.toDouble()
        private const val NP_UP_LIMIT = 4680000.toDouble()

        // 건강보험 상한액
        private const val HC_UP_LIMIT = 3182760.toDouble()
    }
}