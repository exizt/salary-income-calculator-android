package kr.asv.apps.salarycalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.preference.PreferenceManager
import kr.asv.apps.salarycalculator.databases.AppDatabaseHandler
import kr.asv.apps.salarycalculator.model.IncomeTaxDao
import kr.asv.apps.salarycalculator.model.TermDictionaryDao
import kr.asv.salarycalculator.SalaryCalculator
import org.jetbrains.anko.doAsync

/**
 * 전체적인 프로세스를 담당하는 클래스.
 * 싱글톤 으로 생성되어서, 앱의 수명 주기와 동일하게 유지한다.
 */
object Services {
    private const val isDebug = true

    // 계산기 클래스
    val calculator = SalaryCalculator()
    // private set
    private var appDatabasePath = ""
    // Database Handler
    @SuppressLint("StaticFieldLeak")
    lateinit var appDatabaseHandler : AppDatabaseHandler

    private val appPrefs : MutableMap<String, Any> = mutableMapOf()

    /**
     * 기본 세율값
     */
    object DefaultRates {
        var nationalPension: Double = 4.5
        var healthCare: Double = 3.23
        var longTermCare: Double = 8.51
        var employmentCare: Double = 0.65
    }

    /**
     * 앱실행 최초 1회 실행되는 메서드.
     * getInstance 에서 호출한다.
     */
    fun load(context: Context) {
        init()
        loadAppPrefs(context)
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

    private fun init(){
        // SalaryCalculator 의 세율, 상한 하한값 등을 셋팅한다.
        calculator.init()
        debug("[init] 초기화")

        // 세율의 기본값을 가져와서 DefaultRates 에 기록해둔다.
        val rates = calculator.insurance.rates
        DefaultRates.nationalPension = rates.nationalPension * 100
        DefaultRates.healthCare = rates.healthCare * 100
        DefaultRates.longTermCare = rates.longTermCare * 100
        DefaultRates.employmentCare = rates.employmentCare * 100
        debug("[init] 기본 세율 값 셋팅")
    }

    /**
     * 메서드가 호출될 때, TermDictionaryDao 를 새로 생성해서 리턴한다.
     * (가비지 컬렉션을 고려함)
     */
    fun getTermDictionaryDao():TermDictionaryDao{
        val db = SQLiteDatabase.openOrCreateDatabase(appDatabasePath,  null)
        return TermDictionaryDao(db)
    }

    @Suppress("unused")
    fun getIncomeTaxDao(): IncomeTaxDao{
        val db = SQLiteDatabase.openOrCreateDatabase(appDatabasePath,  null)
        return IncomeTaxDao(db)
    }

    /**
     * 세율을 기본값으로 변경해주는 메서드.
     *
     * 설정값에서 변경한 세율값을 기본값으로 돌릴 때 이용한다.
     */
    fun setInsuranceRatesToDefault(){
        debug("[setInsuranceRatesToDefault] 기본값으로 세율 적용")
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

    /**
     * Preferences 에 저장된 값들을 로컬 변수로 불러온다.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun loadAppPrefs(context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        //val prefsAll = prefs.all as MutableMap<String, Any>
        //appPrefs.putAll(prefsAll)

        // 기본 입력값 설정
        assignAppPref(prefs, AppPrefKeys.DefaultInput.family)
        assignAppPref(prefs, AppPrefKeys.DefaultInput.child)
        assignAppPref(prefs, AppPrefKeys.DefaultInput.taxExemption) //비과세액
        assignAppPref(prefs, AppPrefKeys.DefaultInput.severance, "Boolean")

        // 세율 커스텀 설정값
        assignAppPref(prefs, AppPrefKeys.customRateEnable, "Boolean")
        assignAppPref(prefs, AppPrefKeys.CustomRates.nationalPension)
        assignAppPref(prefs, AppPrefKeys.CustomRates.healthCare)
        assignAppPref(prefs, AppPrefKeys.CustomRates.longTermCare)
        assignAppPref(prefs, AppPrefKeys.CustomRates.employmentCare)


    }

    /**
     * Preferences 에 저장된 값을 로컬 변수로 불러온다.
     */
    private fun assignAppPref(prefs: SharedPreferences, key: String, type: String="String"){
        when (type) {
            "String" -> {
                prefs.getString(key,null)?.let { setAppPref(key, it) }
            }
            "Boolean" -> {
                setAppPref(key, prefs.getBoolean(key,false))
            }
            "Int" -> {
                setAppPref(key, prefs.getInt(key,0))
            }
        }
    }

    /**
     * 설정을 모아둔 변수에 값을 대입.
     */
    fun setAppPref(key: String, value: Any){

        // 여기서 로컬 변수에 값을 할당.
        appPrefs[key] = value
    }

    /**
     * '설정 변수' with 'Pref' 에 같이 대입하는 메서드.
     * Read 하는 데서 발생하는 load 를 줄이기 위함. (with 디버깅도 편하게 하고)
     */
    @Suppress("unused")
    fun setAppPrefWithPref(context: Context, key: String, value: Any){
        setPrefValue(context, key, value)

        setAppPref(key, value)
    }

    /**
     * 설정값을 반환.
     * Preferences 를 거치지 않고 갖고 있는 값으로 반환.
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    fun getAppPref(key:String): String {
        //return appPrefs[key]? : ""
        return ""
    }

    /**
     * appPref 에 저장된 값을 반환.
     * (SharedPreferences 를 거치지 않음)
     */
    fun getAppPrefValue(key: String): Any? {
        return appPrefs[key]
    }

    fun isCustomRateMode() : Boolean {
        return getAppPrefValue(AppPrefKeys.customRateEnable) as Boolean
    }

    /**
     * Preferences 에 값을 지정하는 메서드.
     */
    private fun setPrefValue(context: Context, key: String, value: Any){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value.toString())
        editor.apply()
    }

    object AppPrefKeys {
        const val customRateEnable = "rate_settings_enable"
        const val inputBase = "quick_input_criteria"

        object DefaultInput {
            // 부양가족수 (@string/pref_key_quick_family)
            const val family = "quick_settings_family"
            // 20세 이하 자녀수 (@string/pref_key_quick_child)
            const val child = "quick_settings_child"
            // 비과세액 (@string/pref_key_quick_tax_exemption)
            const val taxExemption = "quick_settings_tax_exemption"
            // 퇴직금 포함 여부 (@string/pref_key_quick_severance)
            const val severance = "quick_settings_severance"
        }
        object CustomRates {
            // @string/pref_key_custom_national_pension_rate
            const val nationalPension = "rate_national_pension"
            // @string/pref_key_custom_health_care_rate
            const val healthCare = "rate_health_care"
            // @string/pref_key_custom_long_term_care_rate
            const val longTermCare = "rate_longterm_care"
            // @string/pref_key_custom_employment_care_rate
            const val employmentCare = "rate_employment_care"
        }
    }
}
