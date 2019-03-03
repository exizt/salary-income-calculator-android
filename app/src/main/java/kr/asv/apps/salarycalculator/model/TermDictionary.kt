package kr.asv.apps.salarycalculator.model

data class TermDictionary(
        val TABLE_NAME: String = "terminology_information",
        var id: Int = 0,
        var cid: String? = "",
        var name: String? = "",
        var description: String? = "",
        var process: String? = "",
        var history: String? = ""
)