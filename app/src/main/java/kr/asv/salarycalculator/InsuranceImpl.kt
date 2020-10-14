package kr.asv.salarycalculator

/**
 * 4대 보험 계산 클래스
 * 4대보험요율 및 기준금액 이 변동되면서 계속 동작될 여지가 있으므로,
 * 생성자에서 값을 대입하지 않는다. setter 로 값을 대입하고,
 * calculate 메서드로 연산을 한다.
 *
 * @author EXIZT
 */
class InsuranceImpl internal constructor() : Insurance {
    /**
     * 국민연금
     */
    private var nationalPension = 0.0

    /**
     * 건강보험
     */
    private var healthCare = 0.0

    /**
     * 장기요양보험
     */
    private var longTermCare = 0.0

    /**
     * 고용보험
     */
    private var employmentCare = 0.0

    /**
     * 4대보험요율
     */
    private val rates: InsuranceRates

    // 국민연금 상한, 하한액
    private val NP_DOWN_LIMIT = 300000
    private val NP_UP_LIMIT = 4680000

    // 건강보험 상한액
    private val HC_UP_LIMIT = 3182760

    /**
     * 4대 보험 계산
     * 각각의 보험요율을 계산한다.
     */
    override fun execute(adjustedSalary: Double) {
        calculateNationalPension(adjustedSalary) // 국민연금
        calculateHealthCareWithLongTermCare(adjustedSalary) // 건강보험 과 요양보험
        calculateEmploymentCare(adjustedSalary) // 고용보험
    }

    /**
     * 국민연금 계산식
     *
     * @param adjustedSalary 세금의 기준 봉급액(기본급 - 비과세)
     */
    private fun calculateNationalPension(adjustedSalary: Double) {
        // 최소 최대값 보정
        var adjustedSalary = adjustedSalary
        if (adjustedSalary < NP_DOWN_LIMIT) adjustedSalary = NP_DOWN_LIMIT.toDouble()
        if (adjustedSalary > NP_UP_LIMIT) adjustedSalary = NP_UP_LIMIT.toDouble()

        // 소득월액 천원미만 절사
        adjustedSalary = Math.floor(adjustedSalary / 1000) * 1000

        // 연산식
        val result = adjustedSalary * 0.01 * rates.getNationalPension()

        // 보험료값 원단위 절사
        nationalPension = Math.floor(result / 10) * 10
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
        var result = adjustedSalary * 0.01 * rates.getHealthCare()
        result = Math.floor(result / 10) * 10

        // 상한액
        if (result >= HC_UP_LIMIT) {
            result = HC_UP_LIMIT.toDouble()
        }

        // 보험료값 원단위 절사
        healthCare = Math.floor(result / 10) * 10
    }

    /**
     * 장기요양보험 계산식
     *
     */
    private fun calculateLongTermCare(healthCare: Double) {
        val result = healthCare * 0.01 * rates.getLongTermCare()
        // 원단위 절삭
        longTermCare = Math.floor(result / 10) * 10
    }

    /**
     * 고용보험 계산식
     */
    private fun calculateEmploymentCare(adjustedSalary: Double) {
        val result = adjustedSalary * 0.01 * rates.getEmploymentCare()
        // 원단위 절삭
        employmentCare = Math.floor(result / 10) * 10
    }

    /**
     * 세율 리턴
     * @return InsuranceRates
     */
    override fun getRates(): InsuranceRates {
        return rates
    }

    /**
     * 국민연금
     *
     * @return double
     */
    override fun getNationalPension(): Double {
        return nationalPension
    }

    /**
     * 건강보험료
     *
     * @return double
     */
    override fun getHealthCare(): Double {
        return healthCare
    }

    /**
     * 장기요양보험료
     *
     * @return double
     */
    override fun getLongTermCare(): Double {
        return longTermCare
    }

    /**
     * 고용보험료
     *
     * @return double
     */
    override fun getEmploymentCare(): Double {
        return employmentCare
    }

    override fun get(): Double {
        return getNationalPension() + getHealthCare() + getLongTermCare() + getEmploymentCare()
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        var result = "\n<4대보험 연산 클래스>\n"
        result += """
             국민연금 : ${getNationalPension()}
             
             """.trimIndent()
        result += """
             건강보험 : ${getHealthCare()}
             
             """.trimIndent()
        result += """
             장기요양보험료 : ${getLongTermCare()}
             
             """.trimIndent()
        result += """
             고용보험료 : ${getEmploymentCare()}
             
             """.trimIndent()
        return result
    }

    /**
     * 생성자
     */
    init {
        rates = InsuranceRates()
    }
}