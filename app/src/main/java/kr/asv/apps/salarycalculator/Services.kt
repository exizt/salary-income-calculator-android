package kr.asv.apps.salarycalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
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
 * Created by EXIZT on 2016-04-27.
 */
object Services {
    private const val TAG = "[EXIZT-DEBUG][Services]"
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
            initializeDefaultInsuranceRates(context)

            doAsync {
                 //디비 연결 및 생성과 Assets 을 통한 업데이트
                //debug("[load] > new AppDatabaseHandler")
                appDatabaseHandler = AppDatabaseHandler(context.applicationContext)

                // 데이터베이스의 경로만 갖는다.
                appDatabasePath = appDatabaseHandler.getDatabasePath()

                // 여기서 firebase 관련 처리를 해야함.
                appDatabaseHandler.copyStorageDBFile()

                // 세율 변경이 필요함.
                // preferences 를 이용해야함. 데이터베이스를 읽어와서, 세율 값을 적용시킨다.
                setDefaultInsuranceRates(context)
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
     * 기본 세율 값을 재조정하는 메서드.
     * preferences 에 값이 없으면, Rates 클래스의 값을 참조해 기본값을 넣어두고,
     * 이후부터는, Preferences 의 기본값을 가져와서 Rates 에 보정한다.
     */
    private fun initializeDefaultInsuranceRates(context: Context){
        val rates = calculator.insurance.rates
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if(!prefs.contains(DefaultRatesPrefKey.nationalPension)){
            debug("[initializeDefaultInsuranceRates] 초기화세율 값을 셋팅")
            val editor = prefs.edit()
            editor.putString(DefaultRatesPrefKey.nationalPension,rates.nationalPension.toString())
            editor.putString(DefaultRatesPrefKey.healthCare,rates.healthCare.toString())
            editor.putString(DefaultRatesPrefKey.longTermCare,rates.longTermCare.toString())
            editor.putString(DefaultRatesPrefKey.employmentCare,rates.employmentCare.toString())
            editor.apply()
        } else {
            rates.nationalPension = prefs.getString(Services.DefaultRatesPrefKey.nationalPension, "0").toDouble()
            rates.healthCare = prefs.getString(Services.DefaultRatesPrefKey.healthCare, "0").toDouble()
            rates.longTermCare = prefs.getString(Services.DefaultRatesPrefKey.longTermCare, "0").toDouble()
            rates.employmentCare = prefs.getString(Services.DefaultRatesPrefKey.employmentCare, "0").toDouble()
        }
        //debug("세율",rates)
    }

    /**
     * 세율 초기화 메서드
     * 세율 값을 Pref 의 Default 값으로 재조정한다.
     */
    @Suppress("unused")
    fun initInsuranceRates(context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        initInsuranceRates(prefs)
    }

    /**
     * 세율 초기화 메서드
     * 세율 값을 Pref 의 Default 값으로 재조정한다.
     */
    fun initInsuranceRates(prefs: SharedPreferences){
        debug("[initInsuranceRates] 적용세율 초기화")
        val rates = calculator.insurance.rates
        rates.nationalPension = prefs.getString(Services.DefaultRatesPrefKey.nationalPension, "0").toDouble()
        rates.healthCare = prefs.getString(Services.DefaultRatesPrefKey.healthCare, "0").toDouble()
        rates.longTermCare = prefs.getString(Services.DefaultRatesPrefKey.longTermCare, "0").toDouble()
        rates.employmentCare = prefs.getString(Services.DefaultRatesPrefKey.employmentCare, "0").toDouble()
    }

    /**
     * 세율의 값을 데이터베이스에서 가져와서 설정 Preferences 에 셋팅한다.
     */
    fun setDefaultInsuranceRates(context: Context){
        debug("[setDefaultInsuranceRates] 초기화세율 값을 DB 값에서 배치")

        // 조건절이 이용될 yearmonth ('201902' 같은 형식) 을 만드는 구문.
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR).toString()
        val month = cal.get(Calendar.MONTH).toString().padStart(2,'0')

        // 데이터베이스 에서 4대보험 세율을 조회
        val db = SQLiteDatabase.openOrCreateDatabase(appDatabasePath,  null)
        val cur = db.query("insurance_tax_rates",
                arrayOf("national_pension", "health_care", "long_term_care", "employment_care"), "yearmonth <= ?",
                arrayOf("$year$month"), null, null, "yearmonth desc", "1")
        if (cur.moveToFirst()) {
            val rates = calculator.insurance.rates

            // singleton 으로 묶인 멤버 변수에 값을 지정해준다.
            rates.nationalPension = cur.getDouble(cur.getColumnIndex("national_pension"))
            rates.healthCare = cur.getDouble(cur.getColumnIndex("health_care"))
            rates.longTermCare = cur.getDouble(cur.getColumnIndex("long_term_care"))
            rates.employmentCare = cur.getDouble(cur.getColumnIndex("employment_care"))


            // 세율 정보를 '설정 변수' 에 넣어준다.
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putString(DefaultRatesPrefKey.nationalPension, rates.nationalPension.toString())
            editor.putString(DefaultRatesPrefKey.healthCare, rates.healthCare.toString())
            editor.putString(DefaultRatesPrefKey.longTermCare, rates.longTermCare.toString())
            editor.putString(DefaultRatesPrefKey.employmentCare, rates.employmentCare.toString())
            editor.apply()

            //debug("세율", rates)
        } else {
            debug("[setDefaultInsuranceRates] 쿼리 결과 없음")
        }
        cur.close()
    }

    /**
     * 디버깅 메서드
     * @param msg 메시지
     */
    @Suppress("unused")
    private fun debug(msg: String, msg2 : Any = "") {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            if(msg2 == ""){
                Log.d(TAG, msg)
            } else {
                Log.d(TAG, "$msg $msg2")
            }
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
