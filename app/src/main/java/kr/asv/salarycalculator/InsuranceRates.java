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

    /**
     * 생성자
     */
    InsuranceRates() {
        /*
         * 작성 당시의 세율 초기값.
         * 현재의 세율 값은 다르기 때문에, 데이터베이스를 별도로 이용하거나 세율표를 참조해서 변경하면 됨.
         */
        nationalPension = 4.5;//국민연금
        healthCare = 3.06;//건강보험
        longTermCare = 6.55;//요양보험
        employmentCare = 0.65;//고용보험
    }

    /**
     * 초기 값 셋팅
     */
    @SuppressWarnings("WeakerAccess")
    public void initValues() {
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
