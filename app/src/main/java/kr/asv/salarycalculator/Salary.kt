package kr.asv.salarycalculator

class Salary {

    /**
     * 월급 : 비과세 포함
     */
    var grossSalary: Long = 0
        set(value) {
            grossAnnualSalary = value * 12
            field = value
        }

    /**
     * 연봉 : 비과세 포함
     */
    var grossAnnualSalary: Long = 0
        private set

    /**
     * 월급여액 : 월급 - 비과세
     * (해당금액 = grossSalary - taxExemption)
     */
    var basicSalary: Long = 0
        set(value) {
            basicAnnualSalary = value * 12
            field = value
        }
    
    /**
     * 총급여액 : 월급여액 (월급 - 비과세) * 12
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var basicAnnualSalary: Long = 0
        private set

    /**
     * 월 실수령액
     */
    var netSalary: Long = 0
        set(value) {
            netAnnualSalary = value * 12
            field = value
        }

    /**
     * 연 실수령액
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var netAnnualSalary: Long = 0
        private set

    /**
     * 비과세 금액 (월단위)
     */
    var taxExemption: Long = 0

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
     * taxExemption 등의 값이 미리 지정되어 있어야 한다.
     */
    fun calculate(inputMoney: Long) {
        /*
         * 월급을 먼저 계산해주어야 한다. 연봉을 계산 후 월급을 계산한다.
         * 입력값이 월급일 경우. 연봉은 월급 * 12
         * 입력값이 연봉일 경우는 연봉= 연봉
         * 입력값이 연봉일 경우에는 퇴직금 포함과 미포함 이 있다. 포함인 경우는 월급은 /13이 되고,
         * 실질 연봉은 입력값/13*12 가 된다.
         */
        grossSalary = if (isAnnualBasis) {
            // 연봉 기준 입력인 경우, 월급을 환산
            // 소수점 처리에 대해서는 고민 좀 해봐야함.
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