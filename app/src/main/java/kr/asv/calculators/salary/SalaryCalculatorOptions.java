package kr.asv.calculators.salary;

public class SalaryCalculatorOptions
{
	private double inputMoney;
	private int family;
	private int child;
	private double taxExemption;
	private boolean annualBasis;
	private boolean includedSeverance;
	private boolean debug;
	
	public SalaryCalculatorOptions(){
		this.init();
	}
	public void init()
	{
		this.inputMoney = 2000000;
		this.family = 1;
		this.child = 0;
		this.taxExemption = 100000;
		this.annualBasis = false;
		this.includedSeverance = false;
		this.debug = false;
	}

	public int getFamily()
	{
		return family;
	}
	public void setFamily(int family)
	{
		this.family = family;
	}
	public int getChild()
	{
		return child;
	}
	public void setChild(int child)
	{
		this.child = child;
	}
	public double getInputMoney()
	{
		return inputMoney;
	}
	public void setInputMoney(double inputMoney)
	{
		this.inputMoney = inputMoney;
	}
	public double getTaxExemption()
	{
		return taxExemption;
	}
	public void setTaxExemption(double taxExemption)
	{
		this.taxExemption = taxExemption;
	}
	public boolean isAnnualBasis()
	{
		return annualBasis;
	}
	public void setAnnualBasis(boolean annualBasis)
	{
		this.annualBasis = annualBasis;
	}
	public boolean isIncludedSeverance()
	{
		return includedSeverance;
	}
	public void setIncludedSeverance(boolean includedSeverance)
	{
		this.includedSeverance = includedSeverance;
	}
	public boolean isDebug()
	{
		return debug;
	}
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
	/**
	 * 디버깅을 위한 toString 메서드
	 */
	public String toString()
	{
		String result = "\n<옵션값>\n";
		result += "입력액 : " + inputMoney + "\n";
		result += "가족수(본인포함) : " + family + "\n";
		result += "20세이하자녀수 : " + child + "\n";
		result += "입력기준 : 연급인지 여부 (" + annualBasis + ")\n";
		result += "입력기준 : 퇴직금 포함여부 (" + includedSeverance + ")\n";
		result += "디버깅 여부(" + debug + ")\n";
		return result;
	}
}
