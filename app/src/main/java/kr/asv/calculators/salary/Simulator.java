package kr.asv.calculators.salary;

/**
 * 진입점
 */
public class Simulator
{
	public static void main(String[] args)
	{
		SalaryCalculator calculator = new SalaryCalculator();
		
		// 부양가족수 (본인포함)
		int family = 1;
		// 20세 이하 자녀수
		int child = 0;
		// 입력한 연봉 or 월급
		double inputMoney = 2000000;
		// 비과세액
		double taxExemption = 100000;
		// 입력값이 연봉인지 여부. false 라면 월급입력
		boolean annualBasis = false;
		// 퇴직금 포함여부
		boolean includedSeverance = false;
		
		SalaryCalculatorOptions options = calculator.getOptions();
		options.setInputMoney(inputMoney);
		options.setTaxExemption(taxExemption);
		options.setFamily(family);
		options.setChild(child);
		//noinspection ConstantConditions
		options.setAnnualBasis(annualBasis);
		//noinspection ConstantConditions
		options.setIncludedSeverance(includedSeverance);
		options.setDebug(true);
		
		calculator.run();
	}
}
