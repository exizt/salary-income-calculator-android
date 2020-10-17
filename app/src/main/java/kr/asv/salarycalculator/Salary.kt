package kr.asv.salarycalculator

import kotlin.math.max
import kotlin.math.min

class Salary {
    /**
     * 사용자가 입력한 값
     */
    var inputMoney = 0.0
        set(value) {
            val maximum = 1000000000000000.0
            field = max(value, 0.0) // 음수 방지
            field = min(value, maximum)
        }

    /**
     * 월급(세전, 통상월급)
     */
    var grossSalary = 0.0
        set(value) {
            grossAnnualSalary = value * 12
            field = value
        }

    /**
     * 총연봉
     */
    var grossAnnualSalary = 0.0
        private set

    /**
     * 과세기준액 : 세금 기준 월급
     * (해당금액 = grossSalary - taxExemption)
     */
    var basicSalary = 0.0
        set(value) {
            basicAnnualSalary = value * 12
            field = value
        }
    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var basicAnnualSalary = 0.0
        private set

    /**
     *
     */
    var netSalary = 0.0
        set(value) {
            netAnnualSalary = value * 12
            field = value
        }

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var netAnnualSalary = 0.0
        private set

    /**
     * 비과세 금액 (월단위)
     */
    var taxExemption = 0.0

    /**
     * 입력값이 연 기준인지 여부
     */
    var isAnnualBasis = false

    /**
     * 연봉입력시에 퇴직금 포함인지 여부
     */
    var isSeveranceIncluded = false

    /**
     * 연봉, 월급 계산 하는 메서드
     * inputMoney, taxExemption 등의 값이 미리 지정되어 있어야 한다.
     */
    fun calculate() {
        /*
         * 월급을 먼저 계산해주어야 한다. 연봉을 계산 후 월급을 계산한다.
         * 입력값이 월급일 경우. 연봉은 월급 * 12
         * 입력값이 연봉일 경우는 연봉= 연봉
         * 입력값이 연봉일 경우에는 퇴직금 포함과 미포함 이 있다. 포함인 경우는 월급은 /13이 되고,
         * 실질 연봉은 입력값/13*12 가 된다.
         */
        grossSalary = if (isAnnualBasis) {
            // 연봉 기준 입력인 경우, 월급을 환산
            if (isSeveranceIncluded) {
                // 퇴직금 포함의 연봉인 경우는, 13개월로 나눠야 월소득이 나온다.
                inputMoney / 13
            } else {
                inputMoney / 12
            }
        } else {
            // 월급 기준 입력인 경우
            inputMoney
        }

        // 세금을 계산할 기준의 월급을 구한다.
        basicSalary = grossSalary - taxExemption
    }
}