package kr.asv.apps.salarytax;

import android.content.Context;
import android.util.Log;

import kr.asv.apps.salarytax.databases.WordInfoDBHandler;
import kr.asv.apps.salarytax.databases.TableWordDictionary;
import kr.asv.calculators.salary.SalaryCalculator;

/**
 * 어플 전체적으로 활용되는 기능들을 모아두는 클래스
 * Created by EXIZT on 2016-04-27.
 */
public class Services {
	private static final Services instance = new Services();
	@SuppressWarnings("FieldCanBeLocal")
	private final boolean isDebug = true;
	private final SalaryCalculator calculator = new SalaryCalculator();
	private final TaxCalculatorRates taxCalculatorRates = new TaxCalculatorRates();
	@SuppressWarnings("FieldCanBeLocal")
	private WordInfoDBHandler wordInfoDbHandler = null;
	private TableWordDictionary tableWordDictionary;

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
		this.wordInfoDbHandler = new WordInfoDBHandler(context);
		debug("[Init] > set WordInfoDBHandler");

		this.tableWordDictionary = new TableWordDictionary(wordInfoDbHandler.getDb());
		debug("[Init] > set TableWordDictionary");
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

	public TableWordDictionary getTableWordDictionary() {
		return this.tableWordDictionary;
	}

	public TaxCalculatorRates getTaxCalculatorRates() {
		return this.taxCalculatorRates;
	}

	public class TaxCalculatorRates {
		public double nationalRate;
		public double healthCareRate;
		public double longTermCareRate;
		public double employmentCareRate;
	}
}
