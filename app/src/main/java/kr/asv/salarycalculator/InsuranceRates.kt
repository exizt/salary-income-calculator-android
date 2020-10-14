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
    private var nationalPension = 4.5

    /**
     * 건강보험 요율
     */
    private var healthCare = 3.06

    /**
     * 건강보험>>장기요양 요율
     */
    private var longTermCare = 6.55

    /**
     * 고용보험 요율
     */
    private var employmentCare = 0.65
    fun getNationalPension(): Double {
        return nationalPension
    }

    fun setNationalPension(nationalPension: Double) {
        if (nationalPension > 0) {
            this.nationalPension = nationalPension
        } else {
            this.nationalPension = 0.0
        }
    }

    fun getHealthCare(): Double {
        return healthCare
    }

    fun setHealthCare(healthCare: Double) {
        if (healthCare > 0) {
            this.healthCare = healthCare
        } else {
            this.healthCare = 0.0
        }
    }

    fun getLongTermCare(): Double {
        return longTermCare
    }

    fun setLongTermCare(longTermCare: Double) {
        if (longTermCare > 0) {
            this.longTermCare = longTermCare
        } else {
            this.longTermCare = 0.0
        }
    }

    fun getEmploymentCare(): Double {
        return employmentCare
    }

    fun setEmploymentCare(employmentCare: Double) {
        if (employmentCare > 0) {
            this.employmentCare = employmentCare
        } else {
            this.employmentCare = 0.0
        }
    }

    /**
     * 디버깅용 문자열 리턴 메서드
     * @return String
     */
    override fun toString(): String {
        var result = "\n<보험세율 연산 클래스>\n"
        result += """
             국민연금 : ${nationalPension}
             
             """.trimIndent()
        result += """
             건강보험 : ${healthCare}
             
             """.trimIndent()
        result += """
             요양보험 : ${longTermCare}
             
             """.trimIndent()
        result += """
             고용보험 : ${employmentCare}
             
             """.trimIndent()
        return result
    }
}