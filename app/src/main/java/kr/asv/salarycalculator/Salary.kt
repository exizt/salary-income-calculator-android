package kr.asv.salarycalculator

interface Salary {
    fun calculate()
    fun setInputMoney(inputMoney: Double)
    fun setTaxExemption(taxExemption: Double)
    fun setAnnualBasis(annualBasis: Boolean)
    fun setSeveranceIncluded(severanceIncluded: Boolean)
    val basicSalary: Double
    val grossSalary: Double
    val grossAnnualSalary: Double
    fun setNetSalary(netSalary: Double)
}