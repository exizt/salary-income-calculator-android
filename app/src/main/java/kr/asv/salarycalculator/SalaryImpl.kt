package kr.asv.salarycalculator

class SalaryImpl internal constructor() : Salary {
    /**
     * 사용자가 입력한 값
     */
    private var inputMoney = 0.0

    /**
     * 월급(세전, 통상월급)
     */
    private var grossSalary = 0.0

    /**
     * 총연봉
     */
    private var grossAnnualSalary = 0.0

    /**
     * 과세기준액 : 세금 기준 월급
     * (해당금액 = grossSalary - taxExemption)
     */
    private var basicSalary = 0.0
    var basicAnnualSalary = 0.0
        private set
    private var netSalary = 0.0
    var netAnnualSalary = 0.0
        private set

    /**
     * 비과세 금액 (월단위)
     */
    private var taxExemption = 0.0

    /**
     * 입력값이 연 기준인지 여부
     */
    private var isAnnualBasis = false

    /**
     * 연봉입력시에 퇴직금 포함인지 여부
     */
    private var isSeveranceIncluded = false

    /**
     * 연봉, 월급 계산 하는 메서드
     * inputMoney, taxExemption 등의 값이 미리 지정되어 있어야 한다.
     */
    override fun calculate() {
        /*
         * 월급을 먼저 계산해주어야 한다. 연봉을 계산 후 월급을 계산한다.
         * 입력값이 월급일 경우. 연봉은 월급 * 12
         * 입력값이 연봉일 경우는 연봉= 연봉
         * 입력값이 연봉일 경우에는 퇴직금 포함과 미포함 이 있다. 포함인 경우는 월급은 /13이 되고,
         * 실질 연봉은 입력값/13*12 가 된다.
         */
        // 연봉 기준 입력인 경우, 월급을 환산
        grossSalary = if (isAnnualBasis) {
            /*
             * 퇴직금 포함의 연봉인 경우는, 13개월로 나눠야 월소득이 나온다.
             */
            if (isSeveranceIncluded) {
                inputMoney / 13
            } else {
                inputMoney / 12
            }
        } else {
            inputMoney
        }
        // 총월급 기준으로 실연봉을 다시 계산한다.
        grossAnnualSalary = grossSalary * 12

        // 세금을 계산할 기준의 월급을 구한다.
        basicSalary = grossSalary - taxExemption
        basicAnnualSalary = basicSalary * 12
    }

    override fun setInputMoney(value: Double) {
        var value = value
        val maximum = 1000000000000000.0
        if (value > maximum) value = maximum
        inputMoney = value
    }

    override fun getGrossSalary(): Double {
        return grossSalary
    }

    override fun getGrossAnnualSalary(): Double {
        return grossAnnualSalary
    }

    override fun getBasicSalary(): Double {
        return basicSalary
    }

    fun getNetSalary(): Double {
        return netSalary
    }

    override fun setNetSalary(netSalary: Double) {
        this.netSalary = netSalary
        netAnnualSalary = netSalary * 12
    }

    fun isAnnualBasis(): Boolean {
        return isAnnualBasis
    }

    override fun setAnnualBasis(annualBasis: Boolean) {
        isAnnualBasis = annualBasis
    }

    fun isSeveranceIncluded(): Boolean {
        return isSeveranceIncluded
    }

    override fun setSeveranceIncluded(severanceIncluded: Boolean) {
        isSeveranceIncluded = severanceIncluded
    }

    fun getTaxExemption(): Double {
        return taxExemption
    }

    override fun setTaxExemption(taxExemption: Double) {
        this.taxExemption = taxExemption
    }
}