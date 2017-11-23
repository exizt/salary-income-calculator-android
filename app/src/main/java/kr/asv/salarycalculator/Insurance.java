package kr.asv.salarycalculator;

public interface Insurance {
	void execute(double adjustedSalary);

	double getNationalPension();

	double getHealthCare();

	double getLongTermCare();

	double getEmploymentCare();

	double get();

	String toString();

	InsuranceRates getRates();
}
