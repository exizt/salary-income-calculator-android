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


    public int getFamily() {
        return family;
    }

    /**
     * family 값은 최소 1 이상.
     * @param value int
     */
    public void setFamily(int value) {
        if(value > 1){
            family = value;
        } else {
            family = 0;
        }
    }

    public int getChild() {
        return child;
    }

    public void setChild(int value) {
        if(value > 0){
            child = value;
        } else {
            child = 0;
        }
    }

    double getInputMoney() {
        return inputMoney;
    }

    public void setInputMoney(double value) {
        if(value > 0){
            inputMoney = value;
        } else {
            inputMoney = 0;
        }
    }

    public double getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(double value) {
        if(value > 0){
            taxExemption = value;
        } else {
            taxExemption = 0;
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
        result += "입력액 : " + inputMoney + "\n";
        result += "가족수(본인포함) : " + family + "\n";
        result += "20세이하자녀수 : " + child + "\n";
        result += "입력기준 : 연급인지 여부 (" + annualBasis + ")\n";
        result += "입력기준 : 퇴직금 포함여부 (" + includedSeverance + ")\n";
        result += "디버깅 여부(" + debug + ")\n";
        return result;
    }
}
