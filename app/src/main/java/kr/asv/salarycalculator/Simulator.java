package kr.asv.salarycalculator;

/**
 * 진입점
 */
class Simulator {
    public static void main(String[] args) {
        SalaryCalculator calculator = new SalaryCalculator();

        /*
         * 계산 옵션 정보 입력
         */
        SalaryCalculatorOptions options = calculator.getOptions();
        // 입력한 연봉 or 월급
        options.setInputMoney(2000000);
        // 비과세액
        options.setTaxExemption(100000);
        // 부양가족수 (본인포함)
        options.setFamily(1);
        // 20세 이하 자녀수
        options.setChild(0);
        // 입력값이 연봉인지 여부. false 라면 월급입력
        options.setAnnualBasis(false);
        // 퇴직금 포함여부
        options.setIncludedSeverance(false);
        options.setDebug(true);

        /*
         * 세율 정보 입력 (미 입력시 기본값으로 계산)
         */
        InsuranceRates rates = calculator.getInsurance().getRates();
        rates.setNationalPension(4.5);
        rates.setHealthCare(3.06);
        rates.setLongTermCare(6.55);
        rates.setEmploymentCare(0.65);

        // 계산기 동작
        calculator.run();
    }
}
