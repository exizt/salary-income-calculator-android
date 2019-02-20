package kr.asv.salarycalculator;

public interface Salary {
	void execute();

	void setInputMoney(double inputMoney);

	void setTaxExemption(double taxExemption);

	void setAnnualBasis(boolean annualBasis);

	void setIncludedSeverance(boolean includedSeverance);

	double getBasicSalary();

	@SuppressWarnings("UnusedReturnValue")
	double getGrossSalary();

	@SuppressWarnings("UnusedReturnValue")
	double getGrossAnnualSalary();
}
