package kr.asv.salarycalculator.utils.inputfilter

import android.text.InputFilter
import android.text.Spanned

/**
 * EditText 에서 Filter 를 하는 클래스
 * Created by EXIZT on 2017-11-22.
 */
class InputFilterLongMinMax : InputFilter {
    private var min: Long = 0
    private var max: Long = 0

    @Suppress("unused")
    constructor(min: Int, max: Int) {
        this.min = min.toLong()
        this.max = max.toLong()
    }

    @Suppress("unused")
    constructor(min: Long, max: Long) {
        this.min = min
        this.max = max
    }

    @Suppress("unused")
    constructor(min: Int, max: Long) {
        this.min = min.toLong()
        this.max = max
    }

    @Suppress("unused")
    constructor(min: Int, max: String) {
        this.min = min.toLong()
        this.max = max.toLong()
    }

    @Suppress("unused")
    constructor(min: String, max: String) {
        this.min = java.lang.Long.parseLong(min)
        this.max = java.lang.Long.parseLong(max)
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, _start: Int, _end: Int): CharSequence? {
        try {
            var str = dest.toString() + source.toString()
            if (str.contains(",")) {
                str = str.replace(",".toRegex(), "")
            }
            val input = java.lang.Long.parseLong(str)
            if (isInRange(min, max, input))
                return null
        } catch (ignored: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Long, b: Long, c: Long): Boolean {
        return if (b > a) {
            c in a..b
        } else {
            c in b..a
        }
    }
}