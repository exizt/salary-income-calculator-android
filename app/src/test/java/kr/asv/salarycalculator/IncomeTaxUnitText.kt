package kr.asv.salarycalculator

import org.junit.Test


class IncomeTaxUnitText {
    @Test
    fun unitTest(){
        val incomeTax = IncomeTax()
        val calculator = SalaryCalculator()
        calculator.init()

        val salary = 290 * 10000.0
        val family = 1
        val child = 0
        incomeTax.isDebug = true
        incomeTax.nationalInsurance = calculator.insurance.computeNationalPension(salary)
        incomeTax.calculate(salary, family, child)
    }
}