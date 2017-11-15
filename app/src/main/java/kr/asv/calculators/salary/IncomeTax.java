package kr.asv.calculators.salary;

public interface IncomeTax {
	void execute(double salary, int family, int child);

	void setNationalInsurance(double nationalInsurance);

	double get();

	double getEarnedIncomeTax();

	double getLocalTax();
	
	@SuppressWarnings("SameParameterValue")
	void setDebug(boolean debug);
}
