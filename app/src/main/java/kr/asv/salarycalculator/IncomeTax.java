package kr.asv.salarycalculator;

public interface IncomeTax {
    void execute(double salary, int family, int child);

    void setNationalInsurance(double nationalInsurance);

    void setEarnedIncomeTax(double earnedIncomeTax);

    double get();

    double getEarnedIncomeTax();

    double getLocalTax();

    @SuppressWarnings("SameParameterValue")
    void setDebug(boolean debug);

}
