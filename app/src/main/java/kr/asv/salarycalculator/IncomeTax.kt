package kr.asv.salarycalculator

import kotlin.math.max
import kotlin.math.min


/**
 * 소득세 계산
 *
 * 2020년 이후로 계산과 '간이세액표'의 오차가 발생할 수 밖에 없는 구조가 됨에 따라,
 * 간이 세액표 xls 를 참조로 계산하는 방식으로 변경하고, 이 클래스의 계산식은 점차적으로 폐기하여야 할 듯함.
 *
 * 오차 발생 사유
 * 국민연금을 변수로 사용해야 하는데, 국민연금의 세율, 상한, 하한이 연 단위가 아니라 7월부터 시행된다거나 하는 경우가 생김.
 * 2월에 발생한 간이세액표가 7월에 변경된 국민연금으로 인해 오차가 발생할 수 밖에 없는 구조.
 * (쉽게 말해, 간이세액표 자체가 틀리기 매우 쉽다는 얘기임)
 * 법정 근거는 '간이세액표'(대통령령)이므로 여기에 맞춰서 계산하는 것이 맞는 것임.
 *
 * 어려운 부분은 간이세액표는 게시 시점을 예측할 수 없다는 점... 아무때나 바뀐다...
 * 개정 기록을 살펴보면 1월, 7월, 9월, 9월 등 다양함...
 */
class IncomeTax {

    /**
     * 디버깅유무
     */
    var isDebug = false

    /**
     * 소득세
     */
    var earnedIncomeTax: Long = 0
        set(value) {
            // 소득세 를 지정하고, 동시에 지방세도 같이 계산한다.
            localTax = computeLocalTax(value)
            field = max(value, 0) // 음수 방지
        }

    /**
     * 지방세
     */
    var localTax: Long = 0
        private set(value) {
            field = max(value, 0) // 음수 방지
        }

    /**
     * 계산 동작
     *
     * @param salary Long Salary
     * @param family int family
     * @param child  int child
     */
    fun calculate(salary: Long, family: Int, child: Int) {
        if (isDebug) {
            debug("----------------")
            debug("<소득세 계산 시작>")
            debug("   (옵션) 월급: ", salary)
            debug("   (옵션) 가족수:", family)
            debug("   (옵션) 아이수:", child)
            debug("----------------")
        }
        earnedIncomeTax = if(salary <= 1000 * 10000){
            calculateIncomeTax(salary, family, child)
        } else {
            calculateExtendIncomeTax(salary, family, child)
        }
    }

    /**
     * 월 1천만원 이상의 고급자 계산
     */
    private fun calculateExtendIncomeTax(baseSalary: Long, family: Int, child: Int): Long{
        debug("월 1천 이상 보정 계산")
        // 1억일 때의 간이세액을 계산한다.
        val baseIncomeTax = calculateIncomeTax(1000 * 10000, family, child)

        val incomeTax = when {
            baseSalary <= 14000 * 1000 -> {
                N(baseIncomeTax) + N(baseSalary - 10000 * 1000) * 0.98 * 0.35
            }
            baseSalary <= 28000 * 1000 -> {
                N(baseIncomeTax) + 1372000 + N(baseSalary - 14000 * 1000) * 0.98 * 0.38
            }
            baseSalary <= 30000 * 1000 -> {
                N(baseIncomeTax) + 6585600 + N(baseSalary - 28000 * 1000) * 0.98 * 0.40
            }
            baseSalary <= 450000 * 1000 -> {
                N(baseIncomeTax) + 7385600 + N(baseSalary - 30000 * 1000) * 0.40
            }
            else -> {
                N(baseIncomeTax) + 13385600 + N(baseSalary - 450000 * 1000) * 0.42
            }
        }
        debug("최종 보정 세액:", incomeTax)
        return incomeTax.toLong()
    }

    /**
     * 근로소득세(소득세) 계산식
     *
     * @param salary 비과세를 뺀 월 소득
     * @return Long
     */
    private fun calculateIncomeTax(salary: Long, family: Int, child: Int): Long {

        /*
         * <0. 연산 기준 산출>
         * '총급여액' : 급여액이 속한 급여구간의 중간 값.
         *
         * 급여 구간
         * 150만원 까지는 5000원 단위 간격
         * 300만원 까지는 10000원 단위 간격
         * 1000만원 까지는 20000원 단위 간격
         */
        val baseSalary = when {
            salary <= 150 * 10000 -> {
                calcIntervalMedian(salary, 5000)
            }
            salary <= 300 * 10000 -> {
                calcIntervalMedian(salary, 10000)
            }
            salary < 1000 * 10000 -> {
                calcIntervalMedian(salary, 20000)
            }
            else -> {
                salary
            }
        }
        val baseSalaryY = baseSalary * 12
        debug("월급여액 (구간중간값)(비과세 제외) :", baseSalary)
        debug("연간 총급여액 (구간중간값) :", baseSalaryY)

        // <2. 근로소득공제 금액 산출>
        val basicDeduction = computeEarnedDeduction(baseSalaryY)
        debug("근로소득공제액(연기준):", basicDeduction)

        // <3. 근로소득금액 산출>
        // 근로소득금액 (연) = 총급여액 (연) - 근로소득공제(연)
        val calcSalaryY = baseSalaryY - basicDeduction
        debug("근로소득금액 (연) = 소득기준금액 - 근로소득공제:", calcSalaryY)

        var deduction: Long = 0

        // <4. 종합소득공제(각종 소득공제) 산출 (인적공제, 연금보험료공제, 특별소득공제 등)>
        // 4.1 인적공제
        val personalDeduction = computePersonalDeduction(family, child)
        debug("인적공제:", personalDeduction)
        deduction += personalDeduction

        // 4.2 연금보험료 공제
        val insuranceDeduction = computeInsuranceDeduction(baseSalary)
        debug("연금보험료 공제:", insuranceDeduction)
        deduction += insuranceDeduction

        // 4.3 특별소득공제
        val otherDeduction = computeOtherDeduction(baseSalaryY, family, child)
        debug("특별소득공제:", otherDeduction)
        deduction += otherDeduction
        
        // <5. 과세표준 산출>
        // 근로소득 과세표준 = 근로소득금액 - 종합소득공제(각종 소득공제)
        val agiSalaryY = calcSalaryY - deduction
        debug("과세표준:", agiSalaryY)

        // <6. 결정세액 산출>
        var tax = computeTax(agiSalaryY)
        debug("산출세액:", tax)

        // 근로소득세액공제 처리
        val incomeTaxCredit = computeIncomeTaxCreditLegacy(tax, agiSalaryY)
        debug("근로소득세액공제:", incomeTaxCredit)
        tax -= incomeTaxCredit

        // [6. 간이세액 산출]
        // (산출세액 - 세액공제) / 12 = 간이세액
        debug("결정세액 (연):", tax)
        tax /= 12

        // 십원 미만 절사 (원단위 이하 절사)
        // tax = floor(tax / 10) * 10
        tax = CalcMath.floor(tax, 1)
        debug("결정세액(월. 원단위 절사):", tax)

        // 마이너스 방지
        if (tax < 0) tax = 0

        // 왜인지 모르겠으나 1000 이하는 그냥 감면하는 듯 함.
        if (tax < 1000) tax = 0
        return tax
    }

    /**
     * <근로소득 공제>
     *     [소득세 계산 : 근로소득 금액 산출(근로소득 기초공제 후 남는 근로소득금액(연간)]
     *     근로소득공제를 제한 후의 연간근로소득금액을 구합니다.
     *
     * @return Long
     */
    private fun computeEarnedDeduction(baseSalaryY: Long): Long {
        /*
         * 1)기준 근로소득 공제 산출
         * 연간의 기준 근로소득을 계산한 후, 그 금액에 따른 차등적인 소득공제를 한다.
         */
        val deduction = when {
            baseSalaryY <= 500 * 10000 -> {
                // baseSalaryY * 0.7
                N(baseSalaryY) * 0.7
            }
            baseSalaryY <= 1500 * 10000 -> {
                // 350 * 10000 + (baseSalaryY - 500 * 10000) * 0.4
                N(350 * 10000) + N(baseSalaryY - 500 * 10000) * 0.4
            }
            baseSalaryY <= 4500 * 10000 -> {
                //750 * 10000 + (baseSalaryY - 1500 * 10000) * 0.15
                N(750 * 10000) + N(baseSalaryY - 1500 * 10000) * 0.15
            }
            baseSalaryY <= 10000 * 10000 -> {
                //1200 * 10000 + (baseSalaryY - 4500 * 10000) * 0.05
                N(1200 * 10000) + N(baseSalaryY - 4500 * 10000) * 0.05
            }
            else -> {
                //1475 * 10000 + (baseSalaryY - 10000 * 10000) * 0.02
                N(1475 * 10000) + N(baseSalaryY - 10000 * 10000) * 0.02
            }
        }
        // 상한 2천만원 한도 (개정됨)
        return min(deduction.toLong(), 2000*10000)
    }

    /**
     * <연금보험료 공제>
     *
     * @param baseSalary 기준 월급여액 (구간 중간값)
     * @return Long
     */
    private fun computeInsuranceDeduction(baseSalary: Long): Long {
        return computeNationPension(baseSalary) * 12
    }

    /**
     * 자체적으로 국민연금 계산
     * (개정될 때까지는 예전 방식을 사용한다. 개정되었을 때 변경할 것)
     *
     * @param baseSalary 기준 월급여액 (구간 중간값)
     * @return Long
     */
    private fun computeNationPension(baseSalary: Long): Long{
        var adjustedSalary: Long = baseSalary
        adjustedSalary = max(adjustedSalary, 290000) // 하한 기준 보정
        adjustedSalary = min(adjustedSalary, 4490000) // 상한 기준 보정

        // a = adjustedSalary * 0.045
        val a = N(adjustedSalary) * 0.045
        return CalcMath.floor(a.toLong(),1)
        //return (salaryY * 0.045).toLong()
    }

    /**
     * <인적 공제>
     *
     * 1인당 150만원
     * 인원 기준 : 본인 포함 부양가족 수.
     */
    private fun computePersonalDeduction(family: Int, child: Int): Long {
        return 150 * 10000 * (family + child).toLong()
    }
    
    /**
     * <특별소득공제 산출>
     *     공제대상가족 수를 통해서 간이 계산함.
     *     이 내용은 '근로소득 간이세액표 (조견표)'에 게시된 내용을 따름.
     *     공제대상가족수 = 부양가족수+자녀수 (= 자녀제외 가족수 + 2*자녀수)
     *
     * @param salaryY 총급여액 : 월 급여(비과세소득 제외 급여) x 12
     * @param family 부양가족수
     * @param child 20세 미만 자녀수
     * @return Long
     */
    fun computeOtherDeduction(salaryY: Long, family: Int, child: Int): Long {
        val calcFamily = family + child
        var deduct:N

        if (calcFamily == 1) {
            // 공제대상자 1명인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    //310 * 10000 + salaryY * 0.04
                    N(310 * 10000) + N(salaryY) * 0.04
                }
                salaryY <= 4500 * 10000 -> {
                    //310 * 10000 + salaryY * 0.04 - (salaryY - 3000 * 10000) * 0.05
                    N(310 * 10000) + N(salaryY) * 0.04 - N(salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    //310 * 10000 + salaryY * 0.015
                    N(310 * 10000) + N(salaryY) * 0.015
                }
                salaryY <= 12000 * 10000 -> {
                    //310 * 10000 + salaryY * 0.005
                    N(310 * 10000) + N(salaryY) * 0.005
                }
                else -> {
                    N(0)
                }
            }
        } else if (calcFamily == 2) {
            // 공제대상자 2명인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    N(360 * 10000) + N(salaryY) * 0.04
                }
                salaryY <= 4500 * 10000 -> {
                    N(360 * 10000) + N(salaryY) * 0.04 - N(salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    N(360 * 10000) + N(salaryY) * 0.02
                }
                salaryY <= 12000 * 10000 -> {
                    N(360 * 10000) + N(salaryY) * 0.01
                }
                else -> {
                    N(0)
                }
            }
        } else if(calcFamily >= 3){
            // 공제대상자 3명 이상인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    N(500 * 10000) + N(salaryY) * 0.07
                }
                salaryY <= 4500 * 10000 -> {
                    N(500 * 10000) + N(salaryY) * 0.07 - N(salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    N(500 * 10000) + N(salaryY) * 0.05
                }
                salaryY <= 12000 * 10000 -> {
                    N(500 * 10000) + N(salaryY) * 0.03
                }
                else -> {
                    N(0)
                }
            }
            // 추가공제
            if (salaryY >= 4000 * 10000) {
                deduct += N(salaryY - 4000 * 10000) * 0.04
            }
        } else {
            // 이 경우는 발생하지 않는 경우임. 입력이 잘못된 경우임.
            deduct = N(0)
        }
        return deduct.toLong()
    }

    /**
     * <산출세액 계산>
     *     산출세액 = 과세표준 * 누진세율
     *     과세표준을 기준으로 세율을 적용시켜서 세금을 계산한다.
     * @param agiSalaryY 과세 표준 연봉
     * @return Long
     */
    private fun computeTax(agiSalaryY: Long): Long {

        /**
         * 누진 세율에 맞춘 산출 세액 계산
         *
         * 산출 세액 = 과세표준 * 세율 (누진)
         *
         * 기존에는 합산(덧셈)이었으나, 현재는 간이 계산을 위해 공제(밸셈)식으로 계산.
         * (결과는 동일함)
         */
        val tax = when {
            agiSalaryY <= 1200 * 10000 -> {
                N(agiSalaryY) * 0.06
            }
            agiSalaryY <= 4600 * 10000 -> {
                //72 * 10000 + (taxBase - 1200 * 10000) * 0.15
                N(agiSalaryY) * 0.15 - 108 * 10000
            }
            agiSalaryY <= 8800 * 10000 -> {
                //582 * 10000 + (taxBase - 4600 * 10000) * 0.24
                N(agiSalaryY) * 0.24 - 522 * 10000
            }
            agiSalaryY <= 15000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.35
                N(agiSalaryY) * 0.35 - 1490 * 10000
            }
            agiSalaryY <= 30000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.38
                N(agiSalaryY) * 0.38 - 1940 * 10000
            }
            agiSalaryY <= 50000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.40
                N(agiSalaryY) * 0.40 - 2540 * 10000
            }
            else -> {
                //3760 * 10000 + (taxBase - 15000 * 10000) * 0.42
                N(agiSalaryY) * 0.42 - 3540 * 10000
            }
        }
        // 십원 미만 절사 (원단위 절사)
        //tax = floor(tax / 10) * 10 // 원단위 이하 절사
        return CalcMath.floor(tax.toLong(), 1)
    }

    /**
     * <근로소득 세액 공제 산출>
     *     근로 소득 세액공제 계산식
     *
     *     참고) 개정된 방식이다. 연말정산에 이용되는 방식.
     *     2020년 기준으로는 '근로소득 간이세액표'에 적용하지 않는 듯하다.
     *     computeIncomeTaxCreditLegacy 메서드를 이용할 것.
     *
     *
     * @param tax 산출세액
     * @param baseSalaryY 총급여액
     * @return Long
     */
    @Suppress("unused")
    private fun computeIncomeTaxCredit(tax: Long, baseSalaryY: Long): Long {

        /**
         * 근로소득세액공제 계산
         */
        val calcTaxCredit = if (tax <= 130 * 10000) {
            N(tax) * 0.55
        } else {
            N(715 * 1000) + N(tax - 130 * 10000) * 0.30
        }
        var taxCredit: Long = calcTaxCredit.toLong()

        /**
         * 공제액 상한 설정
         */
        var max: Long = 0
        when {
            baseSalaryY <= 3300 * 10000 -> {
                // 최대 74만원
                max = 74 * 10000
            }
            baseSalaryY <= 7000 * 10000 -> {
                // 즉 상한선이 74 ~ 66만원 사이
                val t = 74 * 10000 - (baseSalaryY - 3300* 10000) * 0.008
                max = t.toLong()
                if(max < 66 * 10000) max = 66 * 10000
            }
            baseSalaryY > 7000 * 10000 -> {
                // 즉 상한성이 66 ~ 50만원 사이
                val t = 66 * 10000 - (baseSalaryY - 7000* 10000) * 0.5
                max - t.toLong()
                if(max < 50 * 10000) max = 50 * 10000
            }
        }

        /**
         * 공제액 상한 보정
         */
        //if (taxCredit >= creditMax) taxCredit = creditMax
        taxCredit = min(taxCredit, max)

        // 원단위 절사
        //taxCredit = floor(taxCredit / 10) * 10 // 원단위 이하 절사
        taxCredit = CalcMath.floor(taxCredit, 1)
        return taxCredit
    }

    /**
     * <근로소득 세액 공제 산출>
     *     근로 소득 세액공제 계산식
     *
     *     참고) 2020년까지는 이 방식을 적용중이다.
     *
     * @param tax 산출세액
     * @param baseSalaryY 총급여액
     * @return Long
     */
    @Suppress("unused")
    private fun computeIncomeTaxCreditLegacy(tax: Long, baseSalaryY: Long): Long {

        /**
         * 근로소득세액공제 계산
         */
        val calcTaxCredit = if (tax <= 50 * 10000) {
            N(tax) * 0.55
        } else {
            N(275 * 1000) + N(tax - 50 * 10000) * 0.30
        }
        var taxCredit: Long = calcTaxCredit.toLong()

        /**
         * 공제액 상한 설정
         */
        var max: Long = 0
        when {
            baseSalaryY <= 5500 * 10000 -> {
                max = 66 * 10000
            }
            baseSalaryY <= 7000 * 10000 -> {
                max = 63 * 10000
            }
            baseSalaryY > 7000 * 10000 -> {
                max = 50 * 10000
            }
        }

        /**
         * 공제액 상한 보정
         */
        //if (taxCredit >= creditMax) taxCredit = creditMax
        taxCredit = min(taxCredit, max)

        // 원단위 절사
        //taxCredit = floor(taxCredit / 10) * 10 // 원단위 이하 절사
        taxCredit = CalcMath.floor(taxCredit, 1)
        return taxCredit
    }

    /**
     * 지방세 계산식
     * 근로소득세의 10%
     *
     * @return Long
     */
    private fun computeLocalTax(incomeTax: Long): Long {
        val calc = N(incomeTax) * 0.1
        // 십원 미만 절사 (원단위 절삭)
        //tax = floor(tax / 10) * 10
        return CalcMath.floor(calc.toLong(), 1)
    }

    /**
     * 구간의 중간값을 계산하는 메서드.
     */
    private fun calcIntervalMedian(value: Long, interval : Int) : Long {
        return (value / interval) * interval + (interval / 2)
    }

    /**
     * 소득세금 전체 금액 (소득세 + 지역세(주민세))
     *
     * @return Long
     */
    fun get(): Long {
        return earnedIncomeTax + localTax
    }

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        return """
            <소득세 연산 클래스>
            소득세 : $earnedIncomeTax
            지방세 : $localTax
        """.trimIndent()
    }

    /**
     * 디버깅 메서드
     */
    private fun debug(msg: Any, msg2: Any = "") {
        if(isDebug){
            println("$msg $msg2")
        }
    }
}