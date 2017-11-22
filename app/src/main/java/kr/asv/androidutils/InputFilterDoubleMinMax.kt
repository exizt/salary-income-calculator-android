package kr.asv.androidutils

import android.text.InputFilter
import android.text.Spanned

/**
 * EditText 에서 Filter 를 하는 클래스
 * Created by EXIZT on 2017-11-22.
 */
class InputFilterDoubleMinMax : InputFilter {
	private var min = 0.0
	private var max: Double = 0.toDouble()

	internal constructor(min: Int, max: Int) {
		this.min = min.toDouble()
		this.max = max.toDouble()
	}

	@Suppress("unused")
	constructor(min: Double, max: Double) {
		this.min = min
		this.max = max
	}

	@Suppress("unused")
	constructor(min: String, max: String) {
		this.min = java.lang.Double.parseDouble(min)
		this.max = java.lang.Double.parseDouble(max)
	}

	override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, _start: Int, _end: Int): CharSequence? {
		try {
			val input = java.lang.Double.parseDouble(dest.toString() + source.toString())
			if (isInRange(min, max, input))
				return null
		} catch (ignored: NumberFormatException) {
		}

		return ""
	}

	private fun isInRange(a: Double, b: Double, c: Double): Boolean {
		return if(b > a){
			c in a..b
		} else {
			c in b..a
		}
	}
}