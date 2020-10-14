package kr.asv.salarycalculator

class SalaryCalculatorOptions {
    /**
     * 입력 금액
     */
    private var inputMoney = 0.0

    /**
     * 가족수 (최소 본인 1명)
     */
    private var family = 1

    /**
     * 20세 이하 자녀수
     */
    private var child = 0

    /**
     * 비과세
     */
    var taxExemption = 0.0
        set(value) {
            field = if (value < 0) {
                0.0
            } else {
                value
            }
        }

    /**
     * 입력금액이 연봉인지 여부. true 일 경우 '연봉' (기본값 false)
     */
    var isAnnualBasis = false

    /**
     * 퇴직금 포함 계산인지 유무 (기본값 false)
     */
    var isIncludedSeverance = false
    var isDebug = false
    fun setIncomeTaxCalculationDisabled(incomeTaxCalculationDisabled: Boolean) {
        isIncomeTaxCalculationDisabled = incomeTaxCalculationDisabled
    }

    @JvmField
    var isIncomeTaxCalculationDisabled = false
    fun getFamily(): Int {
        return family
    }

    /**
     * family 값은 최소 1 이상.
     * @param value int
     */
    fun setFamily(value: Int) {
        family = Math.max(value, 1)
    }

    fun getChild(): Int {
        return child
    }

    fun setChild(value: Int) {
        child = Math.max(value, 0)
    }

    fun getInputMoney(): Double {
        return inputMoney
    }

    fun setInputMoney(value: Double) {
        inputMoney = if (value < 0) {
            0.0
        } else {
            value
        }
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        var result = "\n<옵션값>\n"
        result += "입력 금액 : $inputMoney\n"
        result += "가족 수(본인포함) : $family\n"
        result += "20세이하자녀수 : $child\n"
        result += """
               입력기준 : 연봉인지 여부 (${isAnnualBasis})
               
               """.trimIndent()
        result += """
               입력기준 : 퇴직금 포함여부 (${isIncludedSeverance})
               
               """.trimIndent()
        result += """
               디버깅 여부(${isDebug})
               
               """.trimIndent()
        return result
    }
}