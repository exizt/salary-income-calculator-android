package kr.asv.salarycalculator.utils.inputfilter

import android.text.InputFilter
import android.text.Spanned

@Suppress("unused")
/**
 * EditText 에서 Filter 를 하는 클래스
 * Created by EXIZT on 2017-11-22.
 */
class InputFilterFloatMinMax : InputFilter {
    private var min : Float = 0.0f
    private var max : Float = 0.0f

    @Suppress("unused")
    constructor(min: Float, max: Float) {
        this.min = min
        this.max = max
    }

    constructor(min: Int, max: Float) {
        this.min = min.toFloat()
        this.max = max
    }

    constructor(min: Int, max: Int) {
        this.min = min.toFloat()
        this.max = max.toFloat()
    }

    @Suppress("unused")
    constructor(min: String, max: String) {
        this.min = min.toFloat()
        this.max = max.toFloat()
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, _start: Int, _end: Int): CharSequence? {
        try {
            var str = dest.toString() + source.toString()
            if (str.contains(",")) {
                str = str.replace(",".toRegex(), "")
            }
            val input = str.toFloat()
            if (isInRange(min, max, input))
                return null
        } catch (ignored: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Float, b: Float, c: Float): Boolean {
        return if (b > a) {
            c in a..b
        } else {
            c in b..a
        }
    }
}