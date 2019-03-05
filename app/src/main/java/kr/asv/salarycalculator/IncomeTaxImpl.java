package kr.asv.salarycalculator;

public class IncomeTaxImpl implements IncomeTax {
    /**
     * 소득세
     */
    private double earnedIncomeTax = 0;
    /**
     * 지방세
     */
    private double localTax = 0;
    /**
     * 디버깅유무
     */
    private boolean debug = false;
    /**
     * 국민연금 금액
     */
    private double nationalInsurance = 0;

    /**
     * 계산 동작
     *
     * @param salary double Salary
     * @param family int family
     * @param child  int child
     */
    public void execute(double salary, int family, int child) {
        if (debug) {
            debug("소득세 계산 시작 ");
            debug("월급:" + salary);
            debug("가족수:" + family);
            debug("아이수:" + child);
        }
        this.earnedIncomeTax = calculateEarnedTax(salary, family, child);
        this.localTax = calculateLocalTax(earnedIncomeTax);
    }

    /**
     * 근로소득세(소득세) 계산식
     * TODO: 국민연금 금액 갖고 올 방법 고민
     *
     * @return double
     */
    private double calculateEarnedTax(double salary, int family, int child) {

        /*
         * [1.연산기준 산출]
         * 150만원 까지는 5000원 단위 간격
         * 300만원 까지는 10000원 단위 간격
         * 1000만원 까지는 20000원 단위 간격
         */
        if (salary <= 1500 * 1000) {
            if (salary % 10000 > 5000) {
                salary = Math.floor(salary / 10000) * 10000 + 7500;
            } else {
                salary = Math.floor(salary / 10000) * 10000 + 2500;
            }
        } else if (salary <= 3000 * 1000) {
            salary = Math.floor(salary / 10000) * 10000 + 5000;
        } else if (salary <= 10000 * 1000) {
            if ((salary + 1) % 20000 > 0) {
                salary = Math.floor(salary / 20000) * 20000 + 10000;
            } else {
                salary = Math.floor(salary / 20000) * 20000;
            }
        }
        if (debug)
            debug("확정된 연산기준 기본급:" + salary);

        // [2.연간 근로소득금액 산출]
        // 연간 근로소득금액 = 연간급여 - 비과세 소득(0) - 근로소득공제
        double earned1 = calculateBasicDeduction(salary);
        if (debug)
            debug("연간근로소득금액(기초공제 후):" + earned1);

        // [3. 종합소득공제 산출(인적공제, 연금보험료공제, 특별소득공제 등)]
        double deduction = calculateIntegratedDeduction(salary, family, child);
        if (debug)
            debug("종합소득공제액:" + deduction);

        // [4. 과세표준 산출]
        // 근로소득금액 - 종합소득공제 = 근로소득과세표준
        double taxBaseEarned = earned1 - deduction;
        if (debug)
            debug("과세표준:" + taxBaseEarned);

        // [5. 결정세액 산출]
        double tax = calcTaxEarnedTotal(salary, taxBaseEarned);
        if (debug)
            debug("결정세액:" + tax);

        // [6. 간이세액 산출]
        // (산출세액 - 세액공제) / 12 = 간이세액
        tax = tax / 12;
        // 원단위 이하 절사
        tax = Math.floor(tax / 10) * 10;
        if (debug)
            debug("간이세액:" + tax);

        // 마이너스 방지
        if (tax < 0)
            tax = 0;

        return tax;
    }

    /**
     * [소득세 계산 : 근로소득 금액 산출(근로소득 기초공제 후 남는 근로소득금액(연간)]
     * 근로소득공제를 제한 후의 연간근로소득금액을 구합니다.
     *
     * @return double
     */
    private double calculateBasicDeduction(double salary) {
        /*
         * 1)기준 근로소득 공제 산출
         * 연간의 기준 근로소득을 계산한 후, 그 금액에 따른 차등적인 소득공제를 한다.
         */
        double earnedIncomeBefore = salary * 12;// 연간 기준 금액 산출
        if (debug)
            debug("소득기준금액-공제전(연기준)" + earnedIncomeBefore);
        double deduction;
        if (earnedIncomeBefore <= 500 * 10000) {
            deduction = earnedIncomeBefore * 0.7;
        } else if (earnedIncomeBefore <= 1500 * 10000) {
            deduction = 350 * 10000 + (earnedIncomeBefore - 500 * 10000) * 0.4;
        } else if (earnedIncomeBefore <= 4500 * 10000) {
            deduction = 750 * 10000 + (earnedIncomeBefore - 1500 * 10000) * 0.15;
        } else if (earnedIncomeBefore <= 10000 * 10000) {
            deduction = 1200 * 10000 + (earnedIncomeBefore - 4500 * 10000) * 0.05;
        } else {
            deduction = 1475 * 10000 + (earnedIncomeBefore - 10000 * 10000) * 0.02;
        }
        if (debug)
            debug("근로소득공제액(연기준):" + deduction);

        // 2)줄어든 근로소득금액 산출
        // 근로소득 금액(연간) = 기존의 기준 근로소득 금액 - 근로소득공제
        double adjustedIncomeYearly = earnedIncomeBefore - deduction;// 근로소득금액

        if (debug)
            debug("소득기준금액-근로소득공제 후(연기준):" + adjustedIncomeYearly);

        return adjustedIncomeYearly;
    }

    /**
     * 종합소득공제 산출
     * 인적공제, 연금보험료공제, 특별소득공제 등 의 합계를 반환
     *
     * @return double
     */
    private double calculateIntegratedDeduction(double salary, int family, int child) {
        // 종합소득공제 산출(인적공제, 연금보험료공제, 특별소득공제 등)
        // 인적공제, 연금보험료공제, 특별소득공제 등
        // 1) 인적공제
        double familyDeduction = 150 * 10000 * (family + child);
        if (debug)
            debug("인적공제:" + familyDeduction);

        // 2) 연금보험 공제
        double pensionDeduction = getNationalInsurance() * 12;
        if (debug)
            debug("연금보험료공제:" + pensionDeduction);

        // 3) 특별소득공제
        double deductionEarnedETC = calculateOtherDeduction(salary, family, child);
        if (debug)
            debug("특별소득공제:" + deductionEarnedETC);

        return familyDeduction + pensionDeduction + deductionEarnedETC;
    }

    /**
     * 소득세 중 특별소득공제등
     *
     * @return double
     */
    private double calculateOtherDeduction(double baseSalary, int family, int child) {
        double salaryY = baseSalary * 12;
        int calcFamily = family + child;

        double deduct;
        if (calcFamily >= 3) {
            // 공제대상자 3명 이상인 경우
            if (salaryY <= 3000 * 10000) {
                deduct = 500 * 10000 + salaryY * 0.07;
            } else if (salaryY <= 4500 * 10000) {
                deduct = 500 * 10000 + salaryY * 0.07 - (salaryY - 3000 * 10000) * 0.05;
            } else if (salaryY <= 7000 * 10000) {
                deduct = 500 * 10000 + salaryY * 0.05;
            } else if (salaryY <= 12000 * 10000) {
                deduct = 500 * 10000 + salaryY * 0.03;
            } else {
                deduct = 0;
            }
            // 추가공제
            if (salaryY >= 4000) {
                deduct += (salaryY - 4000 * 10000) * 0.04;
            }
        } else {
            // 공제대상자 2명 이하인 경우
            if (salaryY <= 3000 * 10000) {
                deduct = 360 * 10000 + salaryY * 0.04;
            } else if (salaryY <= 4500 * 10000) {
                deduct = 360 * 10000 + salaryY * 0.04 - (salaryY - 3000 * 10000) * 0.05;
            } else if (salaryY <= 7000 * 10000) {
                deduct = 360 * 10000 + salaryY * 0.02;
            } else if (salaryY <= 12000 * 10000) {
                deduct = 360 * 10000 + salaryY * 0.01;
            } else {
                deduct = 0;
            }
        }
        return deduct;
    }

    /**
     * 소득세 중 산출세액
     *
     * @return double
     */
    private double calcTaxEarnedTotal(double baseSalary, double taxBase) {
        double tax;

        // 세금구간에 따라서, 소득세의 비율 차등 조정
        if (taxBase <= 1200 * 10000) {
            tax = taxBase * 0.06;
        } else if (taxBase <= 4600 * 10000) {
            tax = 72 * 10000 + (taxBase - 1200 * 10000) * 0.15;
        } else if (taxBase <= 8800 * 10000) {
            tax = 582 * 10000 + (taxBase - 4600 * 10000) * 0.24;
        } else if (taxBase <= 15000 * 10000) {
            tax = 1590 * 10000 + (taxBase - 8800 * 10000) * 0.35;
        } else {
            tax = 3760 * 10000 + (taxBase - 15000 * 10000) * 0.38;
        }
        tax = Math.floor(tax / 10) * 10;// 원단위 이하 절사
        if (debug)
            debug("산출세액:" + tax);

        // 근로소득세액공제 처리
        double incomeTaxCredit = calculateTaxCredit(baseSalary, tax);

        if (debug)
            debug("근로소득세액공제:" + incomeTaxCredit);
        tax = tax - incomeTaxCredit;
        return tax;
    }

    /**
     * 근로 소득 세액공제 계산식
     *
     * @return double
     */
    private double calculateTaxCredit(double baseSalary, double tax) {
        double salaryY = baseSalary * 12;
        double taxCredit;
        double creditMax = 0;

        // 근로소득세액공제 한도 지정
        if (salaryY <= 5500 * 10000) {
            creditMax = 660000;
        } else if (salaryY <= 7000 * 10000) {
            creditMax = 630000;
        } else if (salaryY > 7000 * 10000) {
            creditMax = 500000;
        }

        // 근로소득세액공제 처리
        if (tax <= 50 * 10000) {
            taxCredit = tax * 0.55;
        } else {
            taxCredit = 275 * 1000 + (tax - 50 * 10000) * 0.30;
        }

        // 한도를 넘었을 시 한도 내로 재 지정
        if (taxCredit >= creditMax)
            taxCredit = creditMax;
        taxCredit = Math.floor(taxCredit / 10) * 10;// 원단위 이하 절사
        return taxCredit;
    }

    private void debug(String str) {
        System.out.println(str);
    }

    /**
     * 지방세 계산식
     * 근로소득세의 10%
     *
     * @return double
     */
    private double calculateLocalTax(double incomeTax) {
        double tax = incomeTax * 0.1;
        // 원단위 절삭
        tax = Math.floor(tax / 10) * 10;
        return tax;
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    public String toString() {
        String result = "\n<소득세 연산 클래스>\n";
        result += "소득세 : " + this.earnedIncomeTax + "\n";
        result += "지방세 : " + this.localTax + "\n";
        return result;
    }

    /**
     * 소득세
     *
     * @return double
     */
    public double getEarnedIncomeTax() {
        return earnedIncomeTax;
    }

    /**
     * 지역세
     *
     * @return double
     */
    public double getLocalTax() {
        return localTax;
    }

    /**
     * 소득세금 전체 금액 (소득세 + 지역세(주민세))
     *
     * @return double
     */
    public double get() {
        return getEarnedIncomeTax() + getLocalTax();
    }

    /**
     * 국민연금료 입력
     *
     * @param nationalInsurance double
     */
    public void setNationalInsurance(double nationalInsurance) {
        this.nationalInsurance = nationalInsurance;
    }

    private double getNationalInsurance() {
        if (this.nationalInsurance >= 0)
            return this.nationalInsurance;
        else
            return 0;
    }

    /**
     * 디버깅 설정값
     *
     * @param debug boolean
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
