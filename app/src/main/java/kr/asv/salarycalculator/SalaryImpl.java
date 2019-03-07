package kr.asv.salarycalculator;

public class SalaryImpl implements Salary {
    /**
     * 사용자가 입력한 값
     */
    private double inputMoney = 0;
    /**
     * 월급(세전, 통상월급)
     */
    private double grossSalary = 0;
    /**
     * 총연봉
     */
    private double grossAnnualSalary = 0;
    /**
     * 과세기준액 : 세금 기준 월급
     * (해당금액 = grossSalary - taxExemption)
     */
    private double basicSalary = 0;
    private double basicAnnualSalary = 0;
    private double netSalary = 0;
    private double netAnnualSalary = 0;
    /**
     * 비과세 금액 (월단위)
     */
    private double taxExemption = 0;
    /**
     * 입력값이 연 기준인지 여부
     */
    private boolean isAnnualBasis = false;
    /**
     * 연봉입력시에 퇴직금 포함인지 여부
     */
    private boolean isSeveranceIncluded = false;

    SalaryImpl() {
        // setDefaultValues
    }

    /**
     * 연봉, 월급 계산 하는 메서드
     * inputMoney, taxExemption 등의 값이 미리 지정되어 있어야 한다.
     */
    public void calculate() {
        /*
         * 월급을 먼저 계산해주어야 한다. 연봉을 계산 후 월급을 계산한다.
         * 입력값이 월급일 경우. 연봉은 월급 * 12
         * 입력값이 연봉일 경우는 연봉= 연봉
         * 입력값이 연봉일 경우에는 퇴직금 포함과 미포함 이 있다. 포함인 경우는 월급은 /13이 되고,
         * 실질 연봉은 입력값/13*12 가 된다.
         */
        // 연봉 기준 입력인 경우, 월급을 환산
        if (isAnnualBasis) {
            /*
             * 퇴직금 포함의 연봉인 경우는, 13개월로 나눠야 월소득이 나온다.
             */
            if (isSeveranceIncluded) {
                grossSalary = inputMoney / 13;
            } else {
                grossSalary = inputMoney / 12;
            }
        } else {
            grossSalary = inputMoney;
        }
        // 총월급 기준으로 실연봉을 다시 계산한다.
        grossAnnualSalary = grossSalary * 12;

        // 세금을 계산할 기준의 월급을 구한다.
        basicSalary = grossSalary - taxExemption;
        basicAnnualSalary = basicSalary * 12;
    }

    public void setInputMoney(double value) {
        double maximum = 10000000000000000000000000000000000000000000d;
        if(value>maximum) value = maximum;
        this.inputMoney = value;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public double getGrossAnnualSalary() {
        return grossAnnualSalary;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    @SuppressWarnings("unused")
    public double getBasicAnnualSalary() {
        return basicAnnualSalary;
    }

    @SuppressWarnings("unused")
    public double getNetSalary() {
        return netSalary;
    }

    @SuppressWarnings("unused")
    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
        this.netAnnualSalary = netSalary * 12;
    }

    @SuppressWarnings("unused")
    public double getNetAnnualSalary() {
        return netAnnualSalary;
    }

    @SuppressWarnings("unused")
    public boolean isAnnualBasis() {
        return isAnnualBasis;
    }

    public void setAnnualBasis(boolean annualBasis) {
        this.isAnnualBasis = annualBasis;
    }

    @SuppressWarnings("unused")
    public boolean isSeveranceIncluded() {
        return isSeveranceIncluded;
    }

    public void setSeveranceIncluded(boolean severanceIncluded) {
        this.isSeveranceIncluded = severanceIncluded;
    }

    @SuppressWarnings("unused")
    public double getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(double taxExemption) {
        this.taxExemption = taxExemption;
    }

}
