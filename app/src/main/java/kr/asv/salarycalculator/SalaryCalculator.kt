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
    val options = SalaryCalculatorOptions()

    /**
     * 4대 보험
     */
    val insurance = Insurance()

    /**
     * 소득세
     */
    val incomeTax = IncomeTax()

    /**
     * 급여 정보
     */
    val salary = Salary()

    /**
     * 월 실수령액
     */
    var netSalary = 0.0
        private set

    /**
     * 연봉, 월급, 4대보험 계산
     */
    fun calculateSalaryWithInsurances() {
        calculateSalary()
        calculateInsurances(salary.basicSalary)
    }

    /**
     * 연봉 클래스 만 먼저 계산. 연봉/월급 을 계산함.
     * (Options 클래스 를 몰라도 되게 하기 위해서) 필수값들을 하나씩 set 해줌.
     * (역할이 섞이지 않게 한 것임. 자꾸 까먹어서 적어두는 것....)
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun calculateSalary() {
        debug(options)

        // 필수값 지정
        salary.inputMoney = options.inputMoney // 입력 금액
        salary.taxExemption = options.taxExemption // 비과세 금액
        salary.isAnnualBasis = options.isAnnualBasis // 입력값이 연봉인지 유무. true 이면 연봉, false 이면 월급
        salary.isSeveranceIncluded = options.isIncludedSeverance // 퇴직금 포함 금액인지 유무. true 이면 포함된 금액임.

        // 계산
        salary.calculate()
    }

    /**
     * 4대 보험을 계산함.
     * 입력된 기준금액을 통하여 계산함.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun calculateInsurances(basicSalary: Double) {
        insurance.execute(basicSalary)
        debug(insurance)
    }

    /**
     * 계산 실행
     */
    fun run() {
        if (options.isDebug) {
            incomeTax.isDebug = true
        }

        // <1> 연봉, 월급, 4대보험 계산
        calculateSalaryWithInsurances()

        // <2> 소득세, 지방세 계산
        // '계산된 국민연금 납부액'을 넘겨주어야 한다.
        incomeTax.nationalInsurance = insurance.nationalPension
        incomeTax.execute(salary.basicSalary, options.family, options.child)
        debug(incomeTax)

        // <3> 최종 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
        calculateOnlyNetSalary()
    }

    /**
     * 실수령액 계산
     * 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
     */
    fun calculateOnlyNetSalary() {
        netSalary = salary.basicSalary - insurance.get() - incomeTax.get() + options.taxExemption
        salary.netSalary = netSalary
        debug("실수령액 : ", netSalary)
    }

    /**
     * 디버깅 메서드
     */
    private fun debug(msg: Any, msg2: Any = "") {
        if(options.isDebug){
            println("$msg $msg2")
        }
    }
}