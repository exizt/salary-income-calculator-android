package kr.asv.salarycalculator

import java.util.*

/**
 * 실수령액 계산 클래스.
 * 실제적으로 컨트롤 하는 클래스 이다.
 *
 *
 * prepare().run() 을 거친다.
 * prepare() 이후에 setOptions() 등을 호출 하고 마지막에 run 을 실행하면 결과를 산출한다.
 */
class SalaryCalculator {
    private var isPrepared = false

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
    var netSalary: Long = 0
        private set

    fun init(){
        initializeSettings()
    }

    /**
     * 세율, 상한, 하한 설정에 대한 조정
     */
    private fun initializeSettings(){
        val rates = insurance.rates
        val ymd = getYmd()
        // 연도별 인상 추이가 있으므로, 이것에 따른 처리를 해줄 필요가 있음.
        // 앱 로딩 시에 1회 실행함.

        /**
         * 국민 연금 설정
         */
        // 국민연금율
        rates.nationalPension = 4.5
        // 국민 연금 기준액 상한
        insurance.limitNpUp = if(ymd >= 20200701){
            5030000
        } else {
            4860000
        }
        // 국민 연금 기준액 하한
        insurance.limitNpDown = if(ymd >= 20200701){
            320000
        } else {
            310000
        }

        /**
         * 건강 보험, 요양 보험 관련 설정
         */
        // 건강보험율
        rates.healthCare = if(ymd >= 20210101){
            3.43
        } else {
            3.335
        }
        // 건강보험료 상한
        insurance.limitHcUp = if(ymd >= 20200101){
            3322170
        } else {
            3182760
        }
        // 건강보험료 하한
        insurance.limitHcDown = if(ymd >= 20200101){
            18600
        } else {
            18020
        }

        // 요양보험율
        rates.longTermCare = if(ymd >= 20210101){
            11.52
        } else {
            10.25
        }

        /**
         * 고용 보험 관련 설정
         */
        // 고용보험요율
        rates.employmentCare = if(ymd >= 20190101){
            0.8
        } else {
            0.65
        }
    }

    /**
     * 계산 실행
     *
     * @deprecated 앞으로 안 쓰게 될 것임. 소득세 이슈...
     */
    fun run() {
        // 디버깅 옵션
        incomeTax.isDebug = options.isDebug

        // 1. 연봉, 월급
        calculateSalary()

        // 2. 4대 보험 계산
        calculateInsurances(salary.basicSalary)

        // 3. 소득세, 지방세 계산
        // '계산된 국민연금 납부액'을 넘겨주어야 한다.
        // incomeTax.nationalInsurance = insurance.nationalPension // 자체 계산으로 변경함.
        incomeTax.calculate(salary.basicSalary, options.family, options.child)
        debug(incomeTax)

        // 4. 최종 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
        calculateOnlyNetSalary()
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
        salary.taxExemption = options.taxExemption // 비과세 금액
        salary.isAnnualBasis = options.isAnnualBasis // 입력값이 연봉인지 유무. true 이면 연봉, false 이면 월급
        salary.isSeveranceIncluded = options.isIncludedSeverance // 퇴직금 포함 금액인지 유무. true 이면 포함된 금액임.

        // 계산
        salary.calculate(options.inputMoney)
    }

    /**
     * 4대 보험을 계산함.
     * 입력된 기준금액을 통하여 계산함.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun calculateInsurances(basicSalary: Long) {
        insurance.calculate(basicSalary)
        debug(insurance)
    }

    /**
     * 실수령액 계산
     * 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
     */
    private fun calculateOnlyNetSalary() {
        netSalary = salary.basicSalary - insurance.get() - incomeTax.get() + options.taxExemption
        salary.netSalary = netSalary
        debug("실수령액 : ", netSalary)
    }

    /**
     * YYYYMMDD 반환
     */
    private fun getYmd(): Int {
        return Calendar.getInstance().run {
            val y = get(Calendar.YEAR)
            val m = get(Calendar.MONTH) + 1
            val d = get(Calendar.DAY_OF_MONTH)
            y * 10000 + m * 100 + d
            //String.format("%d%d%d",y,m,d).toInt()
            //"{$y}{$m}{$d}".toInt()
        }
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