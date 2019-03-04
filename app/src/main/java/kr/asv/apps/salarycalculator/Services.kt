package kr.asv.apps.salarycalculator

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kr.asv.apps.salarycalculator.databases.AppDatabaseHandler
import kr.asv.apps.salarycalculator.model.TermDictionaryDao
import kr.asv.salarycalculator.SalaryCalculator

/**
 * 전체적인 프로세스를 담당하는 클래스.
 * 싱글톤 으로 생성되어서, 앱의 수명 주기와 동일하게 유지한다.
 * Created by EXIZT on 2016-04-27.
 */
class Services
private constructor() {
    // 계산기 클래스
    val calculator = SalaryCalculator()
    // 세율 클래스
    val taxCalculatorRates = TaxCalculatorRates()
    private var isDebug = false

    //var termDictionaryDao : TermDictionaryDao? = null
    //    private set
    var appDatabasePath = ""

    @Suppress("unused")
    private fun init() {
    }

    /**
     * 앱실행 최초 1회 실행되는 메서드.
     * getInstance 에서 호출한다.
     */
    private fun load(context: Context) {

         //디비 연결
        val appDatabaseHandler = AppDatabaseHandler(context.applicationContext)
        debug("[load] > new AppDatabaseHandler")
        appDatabasePath = appDatabaseHandler.getDatabasePath()

        // 테이블 클래스 생성. (쿼리는 하기 전)
        //this.termDictionaryDao = TermDictionaryDao(appDatabaseHandler.getDb())
        //debug("[load] > new TermDictionaryDao")
    }

    /**
     * 메서드가 호출될 때, TermDictionaryDao 를 새로 생성해서 리턴한다.
     * (가비지 컬렉션을 고려함)
     */
    fun getTermDictionaryDao():TermDictionaryDao{
        val db = SQLiteDatabase.openOrCreateDatabase(appDatabasePath,  null)
        return TermDictionaryDao(db)
    }
    /**
     * 디버깅
     *
     * @param msg message
     */
    private fun debug(msg: String) {

        if (isDebug) {
            Log.e("[EXIZT-DEBUG]", "[Services]$msg")
        }
    }

    @Suppress("unused")
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
