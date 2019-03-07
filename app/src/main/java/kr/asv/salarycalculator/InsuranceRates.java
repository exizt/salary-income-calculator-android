package kr.asv.salarycalculator;

/**
 * 4대 보험요율 의 모델 클래스.
 * getter/setter 가 전부
 * 작성 당시의 세율 초기값 이므로,
 * 현재의 세율값은 다르기 때문에 데이터베이스를 별도로 이용하거나 세율푤르 참조해서 변경하면 됨.
 * 호출하는 곳에서 이 클래스를 멤버, 멤버 로 불러서 변경하면 됨.
 */
public class InsuranceRates {
    /**
     * 국민연금 요율
     */
    private double nationalPension = 4.5;
    /**
     * 건강보험 요율
     */
    private double healthCare = 3.06;
    /**
     * 건강보험>>장기요양 요율
     */
    private double longTermCare = 6.55;
    /**
     * 고용보험 요율
     */
    private double employmentCare = 0.65;

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

    /**
     * 디버깅용 문자열 리턴 메서드
     * @return String
     */
    public String toString(){
        String result = "\n<보험세율 연산 클래스>\n";
        result += "국민연금 : " + this.nationalPension + "\n";
        result += "건강보험 : " + this.healthCare + "\n";
        result += "요양보험 : " + this.longTermCare + "\n";
        result += "고용보험 : " + this.employmentCare + "\n";
        return result;
    }
}
