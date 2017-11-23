package kr.asv.salarycalculator;

public class InsuranceRates {
	/**
	 * 국민연금 요율
	 */
	private double nationalPension;
	/**
	 * 건강보험 요율
	 */
	private double healthCare;
	/**
	 * 건강보험>>장기요양 요율
	 */
	private double longTermCare;
	/**
	 * 고용보험 요율
	 */
	private double employmentCare;

	public InsuranceRates() {
		initValues();
	}

	/**
	 * 초기 값 셋팅
	 */
	@SuppressWarnings("WeakerAccess")
	public void initValues() {
		this.nationalPension = 4.5;//국민연금
		this.healthCare = 3.06;//건강보험
		this.longTermCare = 6.55;//요양보험
		this.employmentCare = 0.65;//고용보험
	}

	public double getNationalPension() {
		return nationalPension;
	}

	public void setNationalPension(double nationalPension) {
		if (nationalPension > 0) {
			this.nationalPension = nationalPension;
		} else {
			this.nationalPension = 0;
		}
	}

	public double getHealthCare() {
		return healthCare;
	}

	public void setHealthCare(double healthCare) {
		if (healthCare > 0) {
			this.healthCare = healthCare;
		} else {
			this.healthCare = 0;
		}
	}

	public double getLongTermCare() {
		return longTermCare;
	}

	public void setLongTermCare(double longTermCare) {
		if (longTermCare > 0) {
			this.longTermCare = longTermCare;
		} else {
			this.longTermCare = 0;
		}

	}

	public double getEmploymentCare() {
		return employmentCare;
	}

	public void setEmploymentCare(double employmentCare) {
		if (employmentCare > 0) {
			this.employmentCare = employmentCare;
		} else {
			this.employmentCare = 0;
		}
	}
}
