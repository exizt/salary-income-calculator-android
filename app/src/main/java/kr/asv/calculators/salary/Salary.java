package kr.asv.calculators.salary;

public interface Salary
{
	public void execute();

	public void setInputMoney(double inputMoney);

	public void setTaxExemption(double taxExemption);

	public void setAnnualBasis(boolean annualBasis);

	public void setIncludedSeverance(boolean includedSeverance);

	public double getBasicSalary();
	
	public double getGrossSalary();
	
	public double getGrossAnnualSalary();
}
