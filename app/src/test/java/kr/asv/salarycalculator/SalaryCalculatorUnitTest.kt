package kr.asv.salarycalculator

import org.junit.Test

class SalaryCalculatorUnitTest {
    @Test
    fun salaryCalculatorTest(){
        val calculator = SalaryCalculator()
        calculator.init()

        /*
         * 계산 옵션 정보 입력
         */
        val options = calculator.options
        // 입력한 연봉 or 월급
        options.inputMoney = 200 * 10000.0
        // 비과세액
        options.taxExemption = 100000.0
        // 부양가족수 (본인포함)
        options.family = 1
        // 20세 이하 자녀수
        options.child = 0
        // 입력값이 연봉인지 여부. false 라면 월급입력
        options.isAnnualBasis = false
        // 퇴직금 포함여부
        options.isIncludedSeverance = false
        options.isDebug = true

        // 계산기 동작
        calculator.run()
    }
}