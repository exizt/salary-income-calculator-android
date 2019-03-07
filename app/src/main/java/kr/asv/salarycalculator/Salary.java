package kr.asv.salarycalculator;

public interface Salary {
    void calculate();

    void setInputMoney(double inputMoney);

    void setTaxExemption(double taxExemption);

    void setAnnualBasis(boolean annualBasis);

    void setSeveranceIncluded(boolean severanceIncluded);

    double getBasicSalary();

    @SuppressWarnings("UnusedReturnValue")
    double getGrossSalary();

    @SuppressWarnings("UnusedReturnValue")
    double getGrossAnnualSalary();
}
