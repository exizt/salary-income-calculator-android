package kr.asv.calculators.salary;

public interface IncomeTax
{
	public void execute(double salary, int family, int child);

	public void setNationalInsurance(double nationalInsurance);

	public double get();
	
	public double getEarnedIncomeTax();
	
	public double getLocalTax();
	
	public void setDebug(boolean debug);
}
