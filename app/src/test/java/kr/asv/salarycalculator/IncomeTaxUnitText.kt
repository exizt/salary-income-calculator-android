package kr.asv.salarycalculator

import org.junit.Test


class IncomeTaxUnitText {
    @Test
    fun unitTest(){
        println(a(34800000.0))

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

    fun a(salaryY: Double): Double{
        return 310 * 10000 + salaryY * 0.04 - (salaryY - 3000 * 10000) * 0.05
    }
}