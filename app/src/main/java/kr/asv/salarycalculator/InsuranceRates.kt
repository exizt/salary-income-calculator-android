package kr.asv.salarycalculator

/**
 * 4대 보험요율 의 모델 클래스.
 * getter/setter 가 전부
 * 작성 당시의 세율 초기값 이므로,
 * 현재의 세율값은 다르기 때문에 데이터베이스를 별도로 이용하거나 세율푤르 참조해서 변경하면 됨.
 * 호출하는 곳에서 이 클래스를 멤버, 멤버 로 불러서 변경하면 됨.
 */
class InsuranceRates {
    /**
     * 국민연금 요율
     */
    var nationalPension = 4.5
        set(value) {
            field = if (value > 0) {
                value
            } else {
                0.0
            }
        }
    /**
     * 건강보험 요율
     */
    var healthCare = 3.06
        set(value) {
            field = if (value > 0) {
                value
            } else {
                0.0
            }
        }

    /**
     * 건강보험>>장기요양 요율
     */
    var longTermCare = 6.55
        set(value) {
            field = if (value > 0) {
                value
            } else {
                0.0
            }
        }

    /**
     * 고용보험 요율
     */
    var employmentCare = 0.65
        set(value) {
            field = if (value > 0) {
                value
            } else {
                0.0
            }
        }

    /**
     * 디버깅용 문자열 리턴 메서드
     * @return String
     */
    override fun toString(): String {
        return """
            <보험세율 연산 클래스>
            국민연금 : $nationalPension
            건강보험 : $healthCare
            요양보험 : $longTermCare
            고용보험 : $employmentCare
        """.trimIndent()
    }
}