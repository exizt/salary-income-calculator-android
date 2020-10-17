package kr.asv.salarycalculator

import junit.framework.Assert.assertEquals
import org.junit.Test


class IncomeTaxUnitText {
    @Test
    fun unitTest(){
        val incomeTax = IncomeTax()
        val calculator = SalaryCalculator()
        calculator.init()

        val salary: Long = 1250 * 1000
        val family = 1
        val child = 0
        incomeTax.isDebug = true
        incomeTax.calculate(salary, family, child)
    }

    /**
     * 특별 공제 테스트
     */
    @Test
    fun computeOtherDeductionUnitTest(){
        val incomeTax = IncomeTax()
        assertEquals(4252000, incomeTax.computeOtherDeduction(3480*10000, 1, 0))
    }

}