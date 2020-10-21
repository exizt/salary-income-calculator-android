package kr.asv.salarycalculator.app.model

data class TermDictionary(
        var id: Int = 0,
        var cid: String? = "",
        var name: String? = "",
        var description: String? = "",
        var process: String? = "",
        var history: String? = ""
)