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

    @SuppressLint("StaticFieldLeak")
    lateinit var appDatabaseHandler : AppDatabaseHandler

    /**
     * 앱실행 최초 1회 실행되는 메서드.
     * getInstance 에서 호출한다.
     */
    fun load(context: Context) {
        // 최초 1회만 시도되도록, appDatabasePath 를 체크함.
        // 체크를 안 하면, mainActivity 가 호출될 때마다 호출 된다...
        if(appDatabasePath==""){
            doAsync {
                 //디비 연결 및 생성과 Assets 을 통한 업데이트
                debug("[load] > new AppDatabaseHandler")
                appDatabaseHandler = AppDatabaseHandler(context.applicationContext)

                // 데이터베이스의 경로만 갖는다.
                appDatabasePath = appDatabaseHandler.getDatabasePath()

                // 여기서 firebase 관련 처리를 해야함.
                appDatabaseHandler.copyStorageDBFile()

                // 세율 변경이 필요함.
                // preferences 를 이용해야함. 데이터베이스를 읽어와서, 세율 값을 적용시킨다.
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
