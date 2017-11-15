package kr.asv.calculators.salary;

/**
 * 4대 보험 계산 클래스
 * 4대보험요율 및 기준금액 이 변동되면서 계속 동작될 여지가 있으므로,
 * 생성자에서 값을 대입하지 않는다. setter 로 값을 대입하고,
 * calculate 메서드로 연산을 한다.
 *
 * @author EXIZT
 */
public class InsuranceImpl implements Insurance {
	/**
	 * 국민연금
	 */
	private double nationalPension = 0;
	/**
	 * 건강보험
	 */
	private double healthCare = 0;
	/**
	 * 장기요양보험
	 */
	private double longTermCare = 0;
	/**
	 * 고용보험
	 */
	private double employmentCare = 0;

	/**
	 * 4대보험요율
	 * TODO 싱글톤으로 해서 유일하게 관리해야 할 것 같다...
	 */
	private final InsuranceRates rates;

	InsuranceImpl() {
		rates = new InsuranceRates();
	}

	/**
	 * 4대 보험 계산
	 */
	public void execute(double adjustedSalary) {
		this.nationalPension = calculateNationalPension(adjustedSalary);// 국민연금
		this.healthCare = calculateHealthCare(adjustedSalary);// 건강보험
		this.longTermCare = calculateLongTermCare(adjustedSalary);// 요양보험
		this.employmentCare = calculateEmploymentCare(adjustedSalary);// 고용보험

	}

	/**
	 * 국민연금 계산식
	 *
	 * @param adjustedSalary 세금의 기준 봉급액(기본급 - 비과세)
	 * @return double
	 */
	private double calculateNationalPension(double adjustedSalary) {
		// 최소 최대값 보정
		if (adjustedSalary < 250000)
			adjustedSalary = 250000;
		if (adjustedSalary > 3980000)
			adjustedSalary = 3980000;

		// 소득월액 천원미만 절사
		adjustedSalary = Math.floor(adjustedSalary / 1000) * 1000;

		// 연산식
		double result = adjustedSalary * 0.01 * rates.getNationalPension();

		// 보험료값 원단위 절사
		result = Math.floor(result / 10) * 10;
		return result;
	}

	/**
	 * 건강보험 계산식
	 *
	 * @param adjustedSalary double
	 * @return double
	 */
	private double calculateHealthCare(double adjustedSalary) {
		double result = adjustedSalary * 0.01 * rates.getHealthCare();

		// 보험료값 원단위 절사
		result = Math.floor(result / 10) * 10;
		return result;
	}

	public InsuranceRates getRates() {
		return rates;
	}

	/**
	 * 장기요양보험 계산식
	 *
	 * @return double
	 */
	private double calculateLongTermCare(double adjustedSalary) {
		double result = this.calculateHealthCare(adjustedSalary) * 0.01 * rates.getLongTermCare();
		// 원단위 절삭
		result = Math.floor(result / 10) * 10;
		return result;
	}

	/**
	 * 고용보험 계산식
	 *
	 * @return double
	 */
	private double calculateEmploymentCare(double adjustedSalary) {
		double result = adjustedSalary * 0.01 * rates.getEmploymentCare();
		// 원단위 절삭
		result = Math.floor(result / 10) * 10;
		return result;
	}

	/**
	 * 국민연금
	 *
	 * @return double
	 */
	public double getNationalPension() {
		return nationalPension;
	}

	/**
	 * 건강보험료
	 *
	 * @return double
	 */
	public double getHealthCare() {
		return healthCare;
	}

	/**
	 * 장기요양보험료
	 *
	 * @return double
	 */
	public double getLongTermCare() {
		return longTermCare;
	}

	/**
	 * 고용보험료
	 *
	 * @return double
	 */
	public double getEmploymentCare() {
		return employmentCare;
	}

	public double get() {
		return getNationalPension() + getHealthCare() + getLongTermCare() + getEmploymentCare();
	}

	/**
	 * 디버깅을 위한 toString 메서드
	 */
	public String toString() {
		String result = "\n<4대보험 연산 클래스>\n";
		result += "국민연금 : " + getNationalPension() + "\n";
		result += "건강보험 : " + getHealthCare() + "\n";
		result += "장기요양보험료 : " + getLongTermCare() + "\n";
		result += "고용보험료 : " + getEmploymentCare() + "\n";
		return result;
	}
}
