package kr.asv.androidutils.inputfilter

import android.text.InputFilter
import android.text.Spanned

/**
 * EditText 에서 Filter 를 하는 클래스
 * Created by EXIZT on 2017-11-22.
 */
class InputFilterMinMax : InputFilter {
    private var min = 0
    private var max = 0

    constructor(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    @Suppress("unused")
    constructor(min: String, max: String) {
        this.min = Integer.parseInt(min)
        this.max = Integer.parseInt(max)
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, _start: Int, _end: Int): CharSequence? {
        try {
            var str = dest.toString() + source.toString()
            if (str.contains(",")) {
                str = str.replace(",".toRegex(), "")
            }
            val input = Integer.parseInt(str)
            if (isInRange(min, max, input))
                return null
        } catch (ignored: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) {
            c in a..b
        } else {
            c in b..a
        }
    }
}