package kr.asv.salarycalculator;

public class SalaryCalculatorOptions {
    /**
     * 입력 금액
     */
    private double inputMoney = 0;
    /**
     * 가족수 (최소 본인 1명)
     */
    private int family = 1;
    /**
     * 20세 이하 자녀수
     */
    private int child = 0;
    /**
     * 비과세
     */
    private double taxExemption = 0;
    /**
     * 입력금액이 연봉인지 여부. true 일 경우 '연봉' (기본값 false)
     */
    private boolean annualBasis = false;
    /**
     * 퇴직금 포함 계산인지 유무 (기본값 false)
     */
    private boolean includedSeverance = false;
    private boolean debug = false;

    public void setIncomeTaxCalculationDisabled(boolean incomeTaxCalculationDisabled) {
        isIncomeTaxCalculationDisabled = incomeTaxCalculationDisabled;
    }

    boolean isIncomeTaxCalculationDisabled = false;

    public int getFamily() {
        return family;
    }

    /**
     * family 값은 최소 1 이상.
     * @param value int
     */
    public void setFamily(int value) {
        family = Math.max(value, 1);
    }

    public int getChild() {
        return child;
    }

    public void setChild(int value) {
        child = Math.max(value, 0);
    }

    @SuppressWarnings("WeakerAccess")
    public double getInputMoney() {
        return inputMoney;
    }

    public void setInputMoney(double value) {
        if(value < 0){
            inputMoney = 0;
        } else {
            inputMoney = value;
        }
    }

    public double getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(double value) {
        if(value < 0){
            taxExemption = 0;
        } else {
            taxExemption = value;
        }
    }

    boolean isAnnualBasis() {
        return annualBasis;
    }

    public void setAnnualBasis(boolean annualBasis) {
        this.annualBasis = annualBasis;
    }

    boolean isIncludedSeverance() {
        return includedSeverance;
    }

    public void setIncludedSeverance(boolean includedSeverance) {
        this.includedSeverance = includedSeverance;
    }

    public boolean isDebug() {
        return debug;
    }

    @SuppressWarnings("SameParameterValue")
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    public String toString() {
        String result = "\n<옵션값>\n";
        result += "입력 금액 : " + inputMoney + "\n";
        result += "가족 수(본인포함) : " + family + "\n";
        result += "20세이하자녀수 : " + child + "\n";
        result += "입력기준 : 연봉인지 여부 (" + annualBasis + ")\n";
        result += "입력기준 : 퇴직금 포함여부 (" + includedSeverance + ")\n";
        result += "디버깅 여부(" + debug + ")\n";
        return result;
    }
}
