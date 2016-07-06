package kr.asv.calculators.salary;

public interface Insurance
{
	public void execute(double adjustedSalary);

	public double getNationalPension();

	public double getHealthCare();

	public double getLongTermCare();

	public double getEmploymentCare();

	public double get();

	public boolean isCalculated();

	public String toString();

	public InsuranceRates getRates();
}
