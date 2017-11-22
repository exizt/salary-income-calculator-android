package kr.asv.apps.salarycalculator

import android.content.Context
import android.util.Log

import kr.asv.apps.salarycalculator.databases.WordDictionaryTable
import kr.asv.apps.salarycalculator.databases.WordInfoDBHandler
import kr.asv.calculators.salary.SalaryCalculator

/**
 * 어플 전체적으로 활용되는 기능들을 모아두는 클래스
 * Created by EXIZT on 2016-04-27.
 */
class Services
/**
 * 생성자 메서드
 */
private constructor() {
	private var isDebug = true
	val calculator = SalaryCalculator()
	val taxCalculatorRates = TaxCalculatorRates()
	var wordDictionaryTable: WordDictionaryTable? = null
		private set

	private fun init() {}

	private fun load(context: Context) {
		//디비 연결
		val wordInfoDbHandler = WordInfoDBHandler(context)
		debug("[Init] > set WordInfoDBHandler")

		// 테이블 클래스 생성. (쿼리는 하기 전)
		this.wordDictionaryTable = WordDictionaryTable(wordInfoDbHandler.db)
		debug("[Init] > set WordDictionaryTable")
	}

	/**
	 * 디버깅
	 *
	 * @param msg message
	 */
	private fun debug(msg: String) {

		if (isDebug) {
			Log.e("[EXIZT-DEBUG]", "[Services]" + msg)
		}
	}

	fun setDebug(debug: Boolean) {
		isDebug = debug
	}

	inner class TaxCalculatorRates {
		var nationalRate: Double = 0.toDouble()
		var healthCareRate: Double = 0.toDouble()
		var longTermCareRate: Double = 0.toDouble()
		var employmentCareRate: Double = 0.toDouble()
	}

	companion object {
		val instance = Services()

		fun getInstance(context: Context): Services {
			instance.load(context)
			return instance
		}
	}
}
