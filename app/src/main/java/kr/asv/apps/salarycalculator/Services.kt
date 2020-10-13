package kr.asv.apps.salarycalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.preference.PreferenceManager
import android.util.Log
import kr.asv.apps.salarycalculator.databases.AppDatabaseHandler
import kr.asv.apps.salarycalculator.model.IncomeTaxDao
import kr.asv.apps.salarycalculator.model.TermDictionaryDao
import kr.asv.salarycalculator.SalaryCalculator
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * 전체적인 프로세스를 담당하는 클래스.
 * 싱글톤 으로 생성되어서, 앱의 수명 주기와 동일하게 유지한다.
 */
object Services {
    private const val isDebug = true

    // 계산기 클래스
    val calculator = SalaryCalculator()
    // 세율 클래스
    val taxCalculatorRates = TaxCalculatorRates()
    // private set
    private var appDatabasePath = ""
    // Database Handler
    @SuppressLint("StaticFieldLeak")
    lateinit var appDatabaseHandler : AppDatabaseHandler

    private val appPrefs : MutableMap<String, String> = mutableMapOf()

    /**
     * 기본 세율값
     */
    object DefaultRates {
        const val nationalPension: Double = 4.5
        const val healthCare: Double = 3.23
        const val longTermCare: Double = 8.51
        const val employmentCare: Double = 0.65
    }

    /**
     * 앱실행 최초 1회 실행되는 메서드.
     * getInstance 에서 호출한다.
     */
    fun load(context: Context) {
        // 앱이 켜지고 최초 1회에만 시도된다. appDatabasePath 가 초기값이 "" 이므로, 아직 값이 정해지기 전이다.
        // 동작이 되고 나면 appDatabasePath 값이 생성된다.
        // 체크를 안 하면, mainActivity 가 호출될 때마다 호출 된다... (액티비티 전환, 뒤로가기, 화면 상하 전환 등...)
        if(appDatabasePath==""){
            // 데이터베이스도 없는지 확인해야 하는데...음...
            // initializeDefaultInsuranceRates(context)

            doAsync {
                 //디비 연결 및 생성과 Assets 을 통한 업데이트
                //debug("[load] > new AppDatabaseHandler")
                appDatabaseHandler = AppDatabaseHandler(context.applicationContext)

                // 데이터베이스의 경로만 갖는다.
                appDatabasePath = appDatabaseHandler.databasePath

                // 여기서 firebase 관련 처리를 해야함.
                appDatabaseHandler.copyFirebaseStorageDbFile()

                // 세율 변경이 필요함.
                // preferences 를 이용해야함. 데이터베이스를 읽어와서, 세율 값을 적용시킨다.
                // setDefaultInsuranceRates(context)
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

    fun getIncomeTaxDao(): IncomeTaxDao{
        val db = SQLiteDatabase.openOrCreateDatabase(appDatabasePath,  null)
        return IncomeTaxDao(db)
    }

    /**
     * 세율 초기화 메서드
     * 세율 값을 Pref 의 Default 값으로 재조정한다.
     */
    fun setInsuranceRatesToDefault(){
        debug("[initInsuranceRates] 적용세율 초기화")
        val rates = calculator.insurance.rates
        rates.nationalPension = DefaultRates.nationalPension
        rates.healthCare = DefaultRates.healthCare
        rates.longTermCare = DefaultRates.longTermCare
        rates.employmentCare = DefaultRates.employmentCare
    }

    @Suppress("SameParameterValue")
    private fun debug(msg: Any) {
        debugLog("Services", msg)
    }

    /**
     * 디버깅 메서드
     * @param msg 메시지
     */
    @Suppress("unused")
    fun debugLog(subTag: String, msg: Any, msg2 : Any = "") {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            Log.d("[EXIZT-SCalculator]", "($subTag) $msg $msg2")
        }
    }

    class TaxCalculatorRates {
        var nationalRate: Double = 0.toDouble()
        var healthCareRate: Double = 0.toDouble()
        var longTermCareRate: Double = 0.toDouble()
        var employmentCareRate: Double = 0.toDouble()
    }

    object DefaultRatesPrefKey {
        const val nationalPension = "rate_default_national_pension"
        const val healthCare = "rate_default_health_care"
        const val longTermCare = "rate_default_long_term_care"
        const val employmentCare = "rate_default_employment_care"
    }
}
