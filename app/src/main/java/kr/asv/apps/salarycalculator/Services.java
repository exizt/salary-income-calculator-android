package kr.asv.apps.salarycalculator;

import android.content.Context;
import android.util.Log;

import kr.asv.apps.salarycalculator.databases.WordDictionaryTable;
import kr.asv.apps.salarycalculator.databases.WordInfoDBHandler;
import kr.asv.calculators.salary.SalaryCalculator;

/**
 * 어플 전체적으로 활용되는 기능들을 모아두는 클래스
 * Created by EXIZT on 2016-04-27.
 */
public class Services {
	private static final Services instance = new Services();
	private boolean isDebug = true;
	private final SalaryCalculator calculator = new SalaryCalculator();
	private final TaxCalculatorRates taxCalculatorRates = new TaxCalculatorRates();
	private WordDictionaryTable wordDictionaryTable;

	/**
	 * 생성자 메서드
	 */
	private Services() {
	}

	public static Services getInstance() {
		return instance;
	}

	@SuppressWarnings("unused")
	public static Services getInstance(Context context) {
		instance.load(context);
		return instance;
	}

	@SuppressWarnings({"unused", "EmptyMethod"})
	private void init() {
	}

	@SuppressWarnings({"unused", "EmptyMethod"})
	private void load(Context context) {
		//디비 연결
		WordInfoDBHandler wordInfoDbHandler = new WordInfoDBHandler(context);
		debug("[Init] > set WordInfoDBHandler");

		// 테이블 클래스 생성. (쿼리는 하기 전)
		this.wordDictionaryTable = new WordDictionaryTable(wordInfoDbHandler.getDb());
		debug("[Init] > set WordDictionaryTable");
	}

	/**
	 * 디버깅
	 *
	 * @param msg message
	 */
	private void debug(String msg) {
		//noinspection ConstantConditions
		if (isDebug) {
			Log.e("[EXIZT-DEBUG]", "[Services]" + msg);
		}
	}

	public SalaryCalculator getCalculator() {
		return calculator;
	}

	public WordDictionaryTable getWordDictionaryTable() {
		return this.wordDictionaryTable;
	}

	public TaxCalculatorRates getTaxCalculatorRates() {
		return this.taxCalculatorRates;
	}

	@SuppressWarnings("unused")
	public void setDebug(boolean debug) {
		isDebug = debug;
	}

	public class TaxCalculatorRates {
		public double nationalRate;
		public double healthCareRate;
		public double longTermCareRate;
		public double employmentCareRate;
	}
}
