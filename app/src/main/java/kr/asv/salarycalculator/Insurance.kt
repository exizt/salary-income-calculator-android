package kr.asv.salarycalculator

interface Insurance {
    fun execute(adjustedSalary: Double)
    val nationalPension: Double
    val healthCare: Double
    val longTermCare: Double
    val employmentCare: Double
    fun get(): Double
    override fun toString(): String
    val rates: InsuranceRates?
}