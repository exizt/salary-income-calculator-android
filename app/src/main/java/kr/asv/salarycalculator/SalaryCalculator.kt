package kr.asv.salarycalculator

/**
 * 실수령액 계산 클래스.
 * 실제적으로 컨트롤 하는 클래스 이다.
 *
 *
 * prepare().run() 을 거친다.
 * prepare() 이후에 setOptions() 등을 호출 하고 마지막에 run 을 실행하면 결과를 산출한다.
 */
class SalaryCalculator {
    /**
     * Options 값들. 가족수, 자녀수, 비과세액 등
     */
    val options: SalaryCalculatorOptions
    val insurance: Insurance
    val incomeTax: IncomeTax
    val salary: Salary
    var netSalary = 0.0
        private set

    /**
     * 연봉, 월급, 4대보험 계산
     */
    fun calculateSalariesWithInsurances() {
        calculateSalaries()
        calculateInsurances(salary.basicSalary)
    }

    /**
     * 연봉 클래스 만 먼저 계산. 연봉/월급 을 계산함.
     * (Options 클래스 를 몰라도 되게 하기 위해서) 필수값들을 하나씩 set 해줌.
     * (역할이 섞이지 않게 한 것임. 자꾸 까먹어서 적어두는 것....)
     */
    fun calculateSalaries() {
        if (options.isDebug) debug(options)

        // 필수값 지정
        salary.setInputMoney(options.getInputMoney()) // 입력 금액
        salary.setTaxExemption(options.taxExemption) // 비과세 금액
        salary.setAnnualBasis(options.isAnnualBasis) // 입력값이 연봉인지 유무. true 이면 연봉, false 이면 월급
        salary.setSeveranceIncluded(options.isIncludedSeverance) // 퇴직금 포함 금액인지 유무. true 이면 포함된 금액임.

        // 계산
        salary.calculate()
    }

    /**
     * 4대 보험을 계산함.
     * 입력된 기준금액을 통하여 계산함.
     */
    fun calculateInsurances(basicSalary: Double) {
        insurance.execute(basicSalary)
        if (options.isDebug) debug(insurance)
    }

    /**
     * 계산 실행
     */
    fun run() {
        if (options.isDebug) {
            incomeTax.setDebug(true)
        }

        // 연봉, 월급, 4대보험 계산
        calculateSalariesWithInsurances()

        // 소득세 계산
        if (!options.isIncomeTaxCalculationDisabled) {
            incomeTax.setNationalInsurance(insurance.nationalPension)
            incomeTax.execute(salary.basicSalary, options.getFamily(), options.getChild())
            if (options.isDebug) debug(incomeTax)
        }

        // 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
        calculateOnlyNetSalary()
    }

    /**
     * 실수령액 계산
     * 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
     */
    fun calculateOnlyNetSalary() {
        netSalary = salary.basicSalary - insurance.get() - incomeTax.get() + options.taxExemption
        salary.setNetSalary(netSalary)
        if (options.isDebug) debug("실수령액 : $netSalary\n")
    }

    private fun debug(obj: Any) {
        println(obj)
    }

    val netAnnualSalary: Double
        get() = netSalary * 12

    /**
     * 생성자
     */
    init {
        options = SalaryCalculatorOptions()
        insurance = InsuranceImpl()
        incomeTax = IncomeTax()
        salary = SalaryImpl()
    }
}