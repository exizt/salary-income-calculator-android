package kr.asv.calculators.salary;

public interface Insurance
{
	void execute(double adjustedSalary);

	double getNationalPension();

	double getHealthCare();

	double getLongTermCare();

	double getEmploymentCare();

	double get();

	boolean isCalculated();

	String toString();

	InsuranceRates getRates();
}
