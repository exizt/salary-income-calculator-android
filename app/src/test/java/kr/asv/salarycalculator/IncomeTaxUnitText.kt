package kr.asv.salarycalculator

import org.junit.Test


class IncomeTaxUnitText {
    @Test
    fun unitTest(){
        val incomeTax = IncomeTax()

        val salary = 290 * 10000.0
        val family = 1
        val child = 0
        incomeTax.nationalInsurance = 0.0


        incomeTax.execute(salary, family, child)
    }
}