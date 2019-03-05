package kr.asv.apps.salarycalculator

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kr.asv.apps.salarycalculator.databases.AppDatabaseHandler
import kr.asv.apps.salarycalculator.model.TermDictionaryDao
import kr.asv.salarycalculator.SalaryCalculator
import org.jetbrains.anko.doAsync

/**
 * 전체적인 프로세스를 담당하는 클래스.
 * 싱글톤 으로 생성되어서, 앱의 수명 주기와 동일하게 유지한다.
 * Created by EXIZT on 2016-04-27.
 */
object Services {
    // 계산기 클래스
    val calculator = SalaryCalculator()
    // 세율 클래스
    val taxCalculatorRates = TaxCalculatorRates()
    private var isDebug = false

    //var termDictionaryDao : TermDictionaryDao? = null
    //    private set
    private var appDatabasePath = ""

    @Suppress("unused")
    private fun init() {
    }

    /**
     * 앱실행 최초 1회 실행되는 메서드.
     * getInstance 에서 호출한다.
     */
    fun load(context: Context) {
        if(appDatabasePath==""){
            doAsync {
                 //디비 연결
                appDatabaseHandler = AppDatabaseHandler(context.applicationContext)
                debug("[load] > new AppDatabaseHandler")
                appDatabasePath = appDatabaseHandler.getDatabasePath()
            }
        }
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
            Log.d("[EXIZT-DEBUG]", "[Services]$msg")
        }
    }

    @Suppress("unused")
    fun setDebug(debug: Boolean) {
        isDebug = debug
    }

    class TaxCalculatorRates {
        var nationalRate: Double = 0.toDouble()
        var healthCareRate: Double = 0.toDouble()
        var longTermCareRate: Double = 0.toDouble()
        var employmentCareRate: Double = 0.toDouble()
    }
}
