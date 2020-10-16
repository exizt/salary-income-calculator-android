package kr.asv.salarycalculator

import kotlin.math.max

class SalaryCalculatorOptions {
    /**
     * 입력 금액
     */
    var inputMoney = 0.0
        set(value) {
            field = if (value < 0) {
                0.0
            } else {
                value
            }
        }

    /**
     * 가족수 (최소 1 이상)
     */
    var family = 1
        set(value) {
            field = max(value, 1)
        }
    /**
     * 20세 이하 자녀수
     */
    var child = 0
        set(value) {
            field = max(value, 0)
        }
    /**
     * 비과세
     */
    var taxExemption = 0.0
        set(value) {
            field = if (value < 0) {
                0.0
            } else {
                value
            }
        }

    /**
     * 입력금액이 연봉인지 여부. true 일 경우 '연봉' (기본값 false)
     */
    var isAnnualBasis = false

    /**
     * 퇴직금 포함 계산인지 유무 (기본값 false)
     */
    var isIncludedSeverance = false
    var isDebug = false

    @JvmField
    var isIncomeTaxCalculationDisabled = false

    /**
     * 디버깅을 위한 toString 메서드
     */
    override fun toString(): String {
        return """
            <옵션값>
            입력 금액 :  $inputMoney
            가족 수(본인포함) :  $family
            20세이하자녀수 :     $child
            입력기준 : 연봉인지 여부 ($isAnnualBasis)
            입력기준 : 퇴직금 포함여부 ($isIncludedSeverance)
            디버깅 여부($isDebug)
        """.trimIndent()
    }
}