package kr.asv.salarycalculator

class IncomeTax {
    /**
     * 소득세
     */
    var earnedIncomeTax = 0.0
        /**
         * 소득세 를 지정하고, 동시에 지방세도 같이 계산한다.
         */
        set(value) {
            localTax = calculateLocalTax(value)
            field = value
        }

    /**
     * 지방세
     */
    var localTax = 0.0
        private set

    /**
     * 디버깅유무
     */
    private var debug = false

    /**
     * 국민연금 금액
     */
    private var nationalInsurance = 0.0

    /**
     * 지방세 계산식
     * 근로소득세의 10%
     *
     * @return double
     */
    private fun calculateLocalTax(incomeTax: Double): Double {
        var tax = incomeTax * 0.1
        // 원단위 절삭
        tax = Math.floor(tax / 10) * 10
        return tax
    }

    /**
     * 계산 동작
     *
     * @param salary double Salary
     * @param family int family
     * @param child  int child
     */
    fun execute(salary: Double, family: Int, child: Int) {
        if (debug) {
            debug("소득세 계산 시작 ")
            debug("월급:$salary")
            debug("가족수:$family")
            debug("아이수:$child")
        }
        earnedIncomeTax = calculateEarnedTax(salary, family, child)
        localTax = calculateLocalTax(earnedIncomeTax)
    }

    /**
     * 근로소득세(소득세) 계산식
     *
     * @return double
     */
    private fun calculateEarnedTax(salary: Double, family: Int, child: Int): Double {

        /*
         * [1.연산기준 산출]
         * 150만원 까지는 5000원 단위 간격
         * 300만원 까지는 10000원 단위 간격
         * 1000만원 까지는 20000원 단위 간격
         */
        var salary = salary
        when {
            salary <= 1500 * 1000 -> {
                salary = if (salary % 10000 > 5000) {
                    Math.floor(salary / 10000) * 10000 + 7500
                } else {
                    Math.floor(salary / 10000) * 10000 + 2500
                }
            }
            salary <= 3000 * 1000 -> {
                salary = Math.floor(salary / 10000) * 10000 + 5000
            }
            salary <= 10000 * 1000 -> {
                salary = if ((salary + 1) % 20000 > 0) {
                    Math.floor(salary / 20000) * 20000 + 10000
                } else {
                    Math.floor(salary / 20000) * 20000
                }
            }
        }
        if (debug) debug("확정된 연산기준 기본급:$salary")

        // [2.연간 근로소득금액 산출]
        // 연간 근로소득금액 = 연간급여 - 비과세 소득(0) - 근로소득공제
        val earned1 = calculateBasicDeduction(salary)
        if (debug) debug("연간근로소득금액(기초공제 후):$earned1")

        // [3. 종합소득공제 산출(인적공제, 연금보험료공제, 특별소득공제 등)]
        val deduction = calculateIntegratedDeduction(salary, family, child)
        if (debug) debug("종합소득공제액:$deduction")

        // [4. 과세표준 산출]
        // 근로소득금액 - 종합소득공제 = 근로소득과세표준
        val taxBaseEarned = earned1 - deduction
        if (debug) debug("과세표준:$taxBaseEarned")

        // [5. 결정세액 산출]
        var tax = calcTaxEarnedTotal(salary, taxBaseEarned)
        if (debug) debug("결정세액:$tax")

        // [6. 간이세액 산출]
        // (산출세액 - 세액공제) / 12 = 간이세액
        tax = tax / 12
        // 원단위 이하 절사
        tax = Math.floor(tax / 10) * 10
        if (debug) debug("간이세액:$tax")

        // 마이너스 방지
        if (tax < 0) tax = 0.0
        return tax
    }

    /**
     * [소득세 계산 : 근로소득 금액 산출(근로소득 기초공제 후 남는 근로소득금액(연간)]
     * 근로소득공제를 제한 후의 연간근로소득금액을 구합니다.
     *
     * @return double
     */
    private fun calculateBasicDeduction(salary: Double): Double {
        /*
         * 1)기준 근로소득 공제 산출
         * 연간의 기준 근로소득을 계산한 후, 그 금액에 따른 차등적인 소득공제를 한다.
         */
        val earnedIncomeBefore = salary * 12 // 연간 기준 금액 산출
        if (debug) debug("소득기준금액-공제전(연기준)$earnedIncomeBefore")
        val deduction: Double
        deduction = if (earnedIncomeBefore <= 500 * 10000) {
            earnedIncomeBefore * 0.7
        } else if (earnedIncomeBefore <= 1500 * 10000) {
            350 * 10000 + (earnedIncomeBefore - 500 * 10000) * 0.4
        } else if (earnedIncomeBefore <= 4500 * 10000) {
            750 * 10000 + (earnedIncomeBefore - 1500 * 10000) * 0.15
        } else if (earnedIncomeBefore <= 10000 * 10000) {
            1200 * 10000 + (earnedIncomeBefore - 4500 * 10000) * 0.05
        } else {
            1475 * 10000 + (earnedIncomeBefore - 10000 * 10000) * 0.02
        }
        if (debug) debug("근로소득공제액(연기준):$deduction")

        // 2)줄어든 근로소득금액 산출
        // 근로소득 금액(연간) = 기존의 기준 근로소득 금액 - 근로소득공제
        val adjustedIncomeYearly = earnedIncomeBefore - deduction // 근로소득금액
        if (debug) debug("소득기준금액-근로소득공제 후(연기준):$adjustedIncomeYearly")
        return adjustedIncomeYearly
    }

    /**
     * 종합소득공제 산출
     * 인적공제, 연금보험료공제, 특별소득공제 등 의 합계를 반환
     *
     * @return double
     */
    private fun calculateIntegratedDeduction(salary: Double, family: Int, child: Int): Double {
        // 종합소득공제 산출(인적공제, 연금보험료공제, 특별소득공제 등)
        // 인적공제, 연금보험료공제, 특별소득공제 등
        // 1) 인적공제
        val familyDeduction = 150 * 10000 * (family + child).toDouble()
        if (debug) debug("인적공제:$familyDeduction")

        // 2) 연금보험 공제
        val pensionDeduction = getNationalInsurance() * 12
        if (debug) debug("연금보험료공제:$pensionDeduction")

        // 3) 특별소득공제
        val deductionEarnedETC = calculateOtherDeduction(salary, family, child)
        if (debug) debug("특별소득공제:$deductionEarnedETC")
        return familyDeduction + pensionDeduction + deductionEarnedETC
    }

    /**
     * 소득세 중 특별소득공제등
     *
     * @return double
     */
    private fun calculateOtherDeduction(baseSalary: Double, family: Int, child: Int): Double {
        val salaryY = baseSalary * 12
        val calcFamily = family + child
        var deduct: Double
        if (calcFamily >= 3) {
            // 공제대상자 3명 이상인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    500 * 10000 + salaryY * 0.07
                }
                salaryY <= 4500 * 10000 -> {
                    500 * 10000 + salaryY * 0.07 - (salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    500 * 10000 + salaryY * 0.05
                }
                salaryY <= 12000 * 10000 -> {
                    500 * 10000 + salaryY * 0.03
                }
                else -> {
                    0.0
                }
            }
            // 추가공제
            if (salaryY >= 4000) {
                deduct += (salaryY - 4000 * 10000) * 0.04
            }
        } else {
            // 공제대상자 2명 이하인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    360 * 10000 + salaryY * 0.04
                }
                salaryY <= 4500 * 10000 -> {
                    360 * 10000 + salaryY * 0.04 - (salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    360 * 10000 + salaryY * 0.02
                }
                salaryY <= 12000 * 10000 -> {
                    360 * 10000 + salaryY * 0.01
                }
                else -> {
                    0.0
                }
            }
        }
        return deduct
    }

    /**
     * 소득세 중 산출세액
     *
     * @return double
     */
    private fun calcTaxEarnedTotal(baseSalary: Double, taxBase: Double): Double {

        // 세금구간에 따라서, 소득세의 비율 차등 조정
        var tax: Double = when {
            taxBase <= 1200 * 10000 -> {
                taxBase * 0.06
            }
            taxBase <= 4600 * 10000 -> {
                72 * 10000 + (taxBase - 1200 * 10000) * 0.15
            }
            taxBase <= 8800 * 10000 -> {
                582 * 10000 + (taxBase - 4600 * 10000) * 0.24
            }
            taxBase <= 15000 * 10000 -> {
                1590 * 10000 + (taxBase - 8800 * 10000) * 0.35
            }
            else -> {
                3760 * 10000 + (taxBase - 15000 * 10000) * 0.38
            }
        }
        tax = Math.floor(tax / 10) * 10 // 원단위 이하 절사
        if (debug) debug("산출세액:$tax")

        // 근로소득세액공제 처리
        val incomeTaxCredit = calculateTaxCredit(baseSalary, tax)
        if (debug) debug("근로소득세액공제:$incomeTaxCredit")
        tax -= incomeTaxCredit
        return tax
    }

    /**
     * 근로 소득 세액공제 계산식
     *
     * @return double
     */
    private fun calculateTaxCredit(baseSalary: Double, tax: Double): Double {
        val salaryY = baseSalary * 12
        var taxCredit: Double
        var creditMax = 0.0

        // 근로소득세액공제 한도 지정
        when {
            salaryY <= 5500 * 10000 -> {
                creditMax = 660000.0
            }
            salaryY <= 7000 * 10000 -> {
                creditMax = 630000.0
            }
            salaryY > 7000 * 10000 -> {
                creditMax = 500000.0
            }
        }

        // 근로소득세액공제 처리
        taxCredit = if (tax <= 50 * 10000) {
            tax * 0.55
        } else {
            275 * 1000 + (tax - 50 * 10000) * 0.30
        }

        // 한도를 넘었을 시 한도 내로 재 지정
        if (taxCredit >= creditMax) taxCredit = creditMax
        taxCredit = Math.floor(taxCredit / 10) * 10 // 원단위 이하 절사
        return taxCredit
    }

    private fun debug(str: String) {
        println(str)
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        var result = "\n<소득세 연산 클래스>\n"
        result += """
             소득세 : $earnedIncomeTax
             
             """.trimIndent()
        result += """
             지방세 : $localTax
             
             """.trimIndent()
        return result
    }

    /**
     * 소득세금 전체 금액 (소득세 + 지역세(주민세))
     *
     * @return double
     */
    fun get(): Double {
        return earnedIncomeTax + localTax
    }

    /**
     * 국민연금료 입력
     *
     * @param nationalInsurance double
     */
    fun setNationalInsurance(nationalInsurance: Double) {
        this.nationalInsurance = nationalInsurance
    }

    private fun getNationalInsurance(): Double {
        return if (nationalInsurance >= 0) nationalInsurance else 0.0
    }

    /**
     * 디버깅 설정값
     *
     * @param debug boolean
     */
    fun setDebug(debug: Boolean) {
        this.debug = debug
    }
}