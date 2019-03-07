package kr.asv.salarycalculator;

/**
 * 실수령액 계산 클래스.
 * 실제적으로 컨트롤 하는 클래스 이다.
 * <p>
 * prepare().run() 을 거친다.
 * prepare() 이후에 setOptions() 등을 호출 하고 마지막에 run 을 실행하면 결과를 산출한다.
 */
@SuppressWarnings("WeakerAccess")
public class SalaryCalculator {
    /**
     * Options 값들. 가족수, 자녀수, 비과세액 등
     */
    private final SalaryCalculatorOptions options;
    private final Insurance insurance;
    private final IncomeTax incomeTax;
    private final Salary salary;
    private double netSalary;

    /**
     * 생성자
     */
    public SalaryCalculator() {
        options = new SalaryCalculatorOptions();
        insurance = new InsuranceImpl();
        incomeTax = new IncomeTaxImpl();
        salary = new SalaryImpl();
    }

    /**
     * 계산 실행
     */
    public void run() {
        if (options.isDebug()) {
            debug(options);
            incomeTax.setDebug(true);
        }

        salary.setAnnualBasis(options.isAnnualBasis());
        salary.setIncludedSeverance(options.isIncludedSeverance());
        salary.setInputMoney(options.getInputMoney());
        salary.setTaxExemption(options.getTaxExemption());
        salary.execute();

        double basicSalary = salary.getBasicSalary();

        insurance.execute(basicSalary);
        if (options.isDebug()) debug(insurance);


        incomeTax.setNationalInsurance(insurance.getNationalPension());
        incomeTax.execute(basicSalary, options.getFamily(), options.getChild());
        if (options.isDebug()) debug(incomeTax);

        // 실수령액 계산 = 월수령액 - 4대보험 - 소득세(+지방세) + 비과세액
        netSalary = basicSalary - insurance.get() - incomeTax.get() + options.getTaxExemption();
        if (options.isDebug()) debug("실수령액 : " + netSalary + "\n");
    }

    private void debug(Object obj) {
        System.out.println(obj);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Insurance getInsurance() {
        return this.insurance;
    }

    @SuppressWarnings("UnusedReturnValue")
    public IncomeTax getIncomeTax() {
        return this.incomeTax;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Salary getSalary() {
        return this.salary;
    }

    /**
     * 옵션 객체를 리턴
     *
     * @return Options
     */
    public SalaryCalculatorOptions getOptions() {
        return this.options;
    }

    @SuppressWarnings("UnusedReturnValue")
    public double getNetSalary() {
        return this.netSalary;
    }

    @SuppressWarnings("unused")
    public double getNetAnnualSalary() {
        return this.netSalary * 12;
    }
}
