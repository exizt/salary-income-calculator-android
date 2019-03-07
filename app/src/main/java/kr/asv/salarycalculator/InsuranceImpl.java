package kr.asv.salarycalculator;

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
     */
    private final InsuranceRates rates;

    // 국민연금 상한, 하한액
    @SuppressWarnings("FieldCanBeLocal")
    private final int NP_DOWN_LIMIT = 300000;
    @SuppressWarnings("FieldCanBeLocal")
    private final int NP_UP_LIMIT = 4680000;
    // 건강보험 상한액
    @SuppressWarnings("FieldCanBeLocal")
    private final int HC_UP_LIMIT = 3182760;
    /**
     * 생성자
     */
    InsuranceImpl(){
        this.rates = new InsuranceRates();
    }

    /**
     * 4대 보험 계산
     * 각각의 보험요율을 계산한다.
     */
    public void execute(double adjustedSalary) {
        calculateNationalPension(adjustedSalary);// 국민연금
        calculateHealthCareWithLongTermCare(adjustedSalary);// 건강보험 과 요양보험
        calculateEmploymentCare(adjustedSalary);// 고용보험
    }

    /**
     * 국민연금 계산식
     *
     * @param adjustedSalary 세금의 기준 봉급액(기본급 - 비과세)
     */
    private void calculateNationalPension(double adjustedSalary) {
        // 최소 최대값 보정
        if (adjustedSalary < NP_DOWN_LIMIT)
            adjustedSalary = NP_DOWN_LIMIT;
        if (adjustedSalary > NP_UP_LIMIT)
            adjustedSalary = NP_UP_LIMIT;

        // 소득월액 천원미만 절사
        adjustedSalary = Math.floor(adjustedSalary / 1000) * 1000;

        // 연산식
        double result = adjustedSalary * 0.01 * rates.getNationalPension();

        // 보험료값 원단위 절사
        this.nationalPension = Math.floor(result / 10) * 10;
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private void calculateHealthCareWithLongTermCare(double adjustedSalary) {
        calculateHealthCare(adjustedSalary);

        calculateLongTermCare(this.healthCare);
    }

    /**
     * 건강보험 계산식
     *
     * @param adjustedSalary double
     */
    private void calculateHealthCare(double adjustedSalary) {
        double result = adjustedSalary * 0.01 * rates.getHealthCare();

        result = Math.floor(result / 10) * 10;

        // 상한액
        if(result >= HC_UP_LIMIT){
            result = HC_UP_LIMIT;
        }

        // 보험료값 원단위 절사
        this.healthCare = Math.floor(result / 10) * 10;
    }

    /**
     * 장기요양보험 계산식
     *
     */
    private void calculateLongTermCare(double healthCare) {
        double result = healthCare * 0.01 * rates.getLongTermCare();
        // 원단위 절삭
        this.longTermCare = Math.floor(result / 10) * 10;
    }

    /**
     * 고용보험 계산식
     */
    private void calculateEmploymentCare(double adjustedSalary) {
        double result = adjustedSalary * 0.01 * rates.getEmploymentCare();
        // 원단위 절삭
        this.employmentCare = Math.floor(result / 10) * 10;
    }

    /**
     * 세율 리턴
     * @return InsuranceRates
     */
    public InsuranceRates getRates() {
        return rates;
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
