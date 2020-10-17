package kr.asv.salarycalculator

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class IncomeTax {
    /**
     * 디버깅유무
     */
    var isDebug = false

    /**
     * 소득세
     */
    var earnedIncomeTax = 0.0
        set(value) {
            // 소득세 를 지정하고, 동시에 지방세도 같이 계산한다.
            localTax = calculateLocalTax(value)
            field = max(value, 0.0) // 음수 방지
        }

    /**
     * 지방세
     */
    var localTax = 0.0
        private set(value) {
            field = max(value, 0.0) // 음수 방지
        }

    /**
     * 국민연금 금액
     */
    var nationalInsurance = 0.0
        set(value) {
            field = max(value, 0.0) // 음수 방지
        }


    /**
     * 계산 동작
     *
     * @param salary double Salary
     * @param family int family
     * @param child  int child
     */
    fun execute(salary: Double, family: Int, child: Int) {
        if (isDebug) {
            debug("----------------")
            debug("<소득세 계산 시작>")
            debug("   (옵션) 월급: ", salary)
            debug("   (옵션) 가족수:", family)
            debug("   (옵션) 아이수:", child)
            debug("----------------")
        }
        earnedIncomeTax = calculateIncomeTax(salary, family, child)
    }

    /**
     * 근로소득세(소득세) 계산식
     *
     * @param salary 비과세를 뺀 월 소득
     * @return double
     */
    private fun calculateIncomeTax(salary: Double, family: Int, child: Int): Double {

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
            salary <= 1000 * 10000 -> {
                calcIntervalMedian(salary, 20000)
            }
            else -> {
                salary
            }
        }
        val baseSalaryY = baseSalary * 12
        debug("계산 기준 월 급여 :", baseSalary)
        debug("계산 기준 연 급여 :", baseSalaryY)

        // <2. 근로소득공제 금액 산출>
        val basicDeduction = getEarnedDeduction(baseSalaryY)
        debug("근로소득공제액(연기준):", basicDeduction)

        // <3. 근로소득금액 산출>
        // 근로소득금액 (연) = 총급여액 (연) - 근로소득공제(연)
        val calcSalaryY = baseSalaryY - basicDeduction
        debug("근로소득금액 (연) = 소득기준금액 - 근로소득공제:", calcSalaryY)

        // <4. 종합소득공제(각종 소득공제) 산출 (인적공제, 연금보험료공제, 특별소득공제 등)>
        // 4.1 연금보험료 공제
        var deduction = getInsuranceDeduction()
        debug("연금보험료 공제:", deduction)

        // 4.2 인적공제
        val personalDeduction = getPersonalDeduction(family, child)
        debug("인적공제:", personalDeduction)
        deduction += personalDeduction
        
        // 특별소득공제
        val otherDeduction = calculateOtherDeduction(baseSalaryY, family, child)
        debug("특별소득공제:", otherDeduction)
        deduction += otherDeduction
        
        // <5. 과세표준 산출>
        // 근로소득 과세표준 = 근로소득금액 - 종합소득공제(각종 소득공제)
        val agiSalaryY = calcSalaryY - deduction
        debug("과세표준:", agiSalaryY)

        // <6. 결정세액 산출>
        var tax = calculateTax(agiSalaryY)
        debug("산출세액:", tax)

        // 근로소득세액공제 처리
        val incomeTaxCredit = calculateTaxCredit(agiSalaryY, tax)
        debug("근로소득세액공제:", incomeTaxCredit)
        tax -= incomeTaxCredit

        // [6. 간이세액 산출]
        // (산출세액 - 세액공제) / 12 = 간이세액
        debug("결정세액 (연):", tax)
        tax /= 12

        // 십원 미만 절사 (원단위 이하 절사)
        // tax = floor(tax / 10) * 10
        tax = CalcMath.roundFloor(tax, -1)
        debug("결정세액(월. 원단위 절사):", tax)

        // 마이너스 방지
        if (tax < 0) tax = 0.0
        return tax
    }

    /**
     * <근로소득 공제>
     *     [소득세 계산 : 근로소득 금액 산출(근로소득 기초공제 후 남는 근로소득금액(연간)]
     *     근로소득공제를 제한 후의 연간근로소득금액을 구합니다.
     *
     * @return double
     */
    private fun getEarnedDeduction(baseSalaryY: Double): Double {
        /*
         * 1)기준 근로소득 공제 산출
         * 연간의 기준 근로소득을 계산한 후, 그 금액에 따른 차등적인 소득공제를 한다.
         */
        var deduction = when {
            baseSalaryY <= 500 * 10000 -> {
                baseSalaryY * 0.7
            }
            baseSalaryY <= 1500 * 10000 -> {
                350 * 10000 + (baseSalaryY - 500 * 10000) * 0.4
            }
            baseSalaryY <= 4500 * 10000 -> {
                750 * 10000 + (baseSalaryY - 1500 * 10000) * 0.15
            }
            baseSalaryY <= 10000 * 10000 -> {
                1200 * 10000 + (baseSalaryY - 4500 * 10000) * 0.05
            }
            else -> {
                1475 * 10000 + (baseSalaryY - 10000 * 10000) * 0.02
            }
        }
        // 상한 2천만원 한도 (개정됨)
        deduction = min(deduction, 2000*10000.0)

        return deduction
    }

    /**
     * <연금보험료 공제>
     *
     * @return double
     */
    private fun getInsuranceDeduction(): Double {
        return nationalInsurance * 12
    }

    /**
     * <인적 공제>
     *
     * 1인당 150만원
     * 인원 기준 : 본인 포함 부양가족 수.
     */
    private fun getPersonalDeduction(family: Int, child: Int): Double {
        return 150 * 10000 * (family + child).toDouble()
    }
    
    /**
     * <특별소득공제 산출>
     *     공제대상가족 수를 통해서 간이 계산함.
     *     이 내용은 '근로소득 간이세액표 (조견표)'에 게시된 내용을 따름.
     *     공제대상가족수 = 부양가족수+자녀수 (= 자녀제외 가족수 + 2*자녀수)
     *
     * @param salaryY 총급여액
     * @param family 부양가족수
     * @param child 20세 미만 자녀수
     * @return double
     */
    private fun calculateOtherDeduction(salaryY: Double, family: Int, child: Int): Double {
        val calcFamily = family + child
        var deduct: Double

        if (calcFamily == 1) {
            // 공제대상자 1명인 경우
            deduct = when {
                salaryY <= 3000 * 10000 -> {
                    310 * 10000 + salaryY * 0.04
                }
                salaryY <= 4500 * 10000 -> {
                    310 * 10000 + salaryY * 0.04 - (salaryY - 3000 * 10000) * 0.05
                }
                salaryY <= 7000 * 10000 -> {
                    310 * 10000 + salaryY * 0.015
                }
                salaryY <= 12000 * 10000 -> {
                    310 * 10000 + salaryY * 0.005
                }
                else -> {
                    0.0
                }
            }
        } else if (calcFamily == 2) {
            // 공제대상자 2명인 경우
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
        } else if(calcFamily >= 3){
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
            if (salaryY >= 4000 * 10000) {
                deduct += (salaryY - 4000 * 10000) * 0.04
            }
        } else {
            // 이 경우는 발생하지 않는 경우임. 입력이 잘못된 경우임.
            deduct = 0.0
        }
        return deduct
    }

    /**
     * <산출세액 계산>
     *     산출세액 = 과세표준 * 누진세율
     *     과세표준을 기준으로 세율을 적용시켜서 세금을 계산한다.
     * @param agiSalaryY 과세 표준 연봉
     * @return double
     */
    fun calculateTax(agiSalaryY: Double): Double {

        /**
         * 누진 세율에 맞춘 산출 세액 계산
         *
         * 산출 세액 = 과세표준 * 세율 (누진)
         *
         * 기존에는 합산(덧셈)이었으나, 현재는 간이 계산을 위해 공제(밸셈)식으로 계산.
         * (결과는 동일함)
         */
        var tax: Double = when {
            agiSalaryY <= 1200 * 10000 -> {
                agiSalaryY * 0.06
            }
            agiSalaryY <= 4600 * 10000 -> {
                //72 * 10000 + (taxBase - 1200 * 10000) * 0.15
                agiSalaryY * 0.15 - 108 * 10000
            }
            agiSalaryY <= 8800 * 10000 -> {
                //582 * 10000 + (taxBase - 4600 * 10000) * 0.24
                agiSalaryY * 0.24 - 522 * 10000
            }
            agiSalaryY <= 15000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.35
                agiSalaryY * 0.35 - 1490 * 10000
            }
            agiSalaryY <= 30000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.38
                agiSalaryY * 0.38 - 1940 * 10000
            }
            agiSalaryY <= 50000 * 10000 -> {
                //1590 * 10000 + (taxBase - 8800 * 10000) * 0.40
                agiSalaryY * 0.40 - 2540 * 10000
            }
            else -> {
                //3760 * 10000 + (taxBase - 15000 * 10000) * 0.42
                agiSalaryY * 0.42 - 3540 * 10000
            }
        }
        // 십원 미만 절사 (원단위 절사)
        //tax = floor(tax / 10) * 10 // 원단위 이하 절사
        tax = CalcMath.roundFloor(tax, -1)
        // debug("산출세액:", tax)

        return tax
    }

    /**
     * <세액 공제 산출>
     *
     * 근로 소득 세액공제 계산식
     *
     * @param baseSalaryY 총급여액
     * @param tax 산출세액
     * @return double
     */
    private fun calculateTaxCredit(baseSalaryY: Double, tax: Double): Double {
        var taxCredit: Double
        var creditMax = 0.0

        // 근로소득세액공제 상한 지정
        when {
            baseSalaryY <= 3300 * 10000 -> {
                // 최대 74만원
                creditMax = 740000.0
            }
            baseSalaryY <= 7000 * 10000 -> {
                // 즉 상한선이 74 ~ 66만원 사이
                creditMax = 74*10000 - (baseSalaryY - 3300* 10000) * 0.008
                if(creditMax < 66 * 10000) creditMax = 66 * 10000.0
            }
            baseSalaryY > 7000 * 10000 -> {
                // 즉 상한성이 66 ~ 50만원 사이
                creditMax = 66 * 10000 - (baseSalaryY - 7000* 10000) * 0.5
                if(creditMax < 50 * 10000) creditMax = 50 * 10000.0
            }
        }

        // 근로소득세액공제 처리
        taxCredit = if (tax <= 130 * 10000) {
            tax * 0.55
        } else {
            715 * 1000 + (tax - 130 * 10000) * 0.30
        }

        // 한도를 넘었을 시 한도 내로 재 지정
        //if (taxCredit >= creditMax) taxCredit = creditMax
        taxCredit = min(taxCredit, creditMax)

        // 원단위 절사
        //taxCredit = floor(taxCredit / 10) * 10 // 원단위 이하 절사
        taxCredit = CalcMath.roundFloor(taxCredit, -1)
        return taxCredit
    }


    /**
     * 지방세 계산식
     * 근로소득세의 10%
     *
     * @return double
     */
    private fun calculateLocalTax(incomeTax: Double): Double {
        var tax = incomeTax * 0.1
        // 십원 미만 절사 (원단위 절삭)
        //tax = floor(tax / 10) * 10
        tax = CalcMath.roundFloor(tax, -1)
        return tax
    }

    /**
     * 구간의 중간값을 계산하는 메서드.
     */
    private fun calcIntervalMedian(value: Double, interval : Int) : Double {
        return floor(value / interval) * interval + (interval / 2)
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