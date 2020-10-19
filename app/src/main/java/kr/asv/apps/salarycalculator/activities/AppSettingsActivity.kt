package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import kr.asv.androidutils.inputfilter.InputFilterDoubleMinMax
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.androidutils.inputfilter.InputFilterMinMax
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services

class AppSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            //setPreferencesFromResource(R.xml.root_preferences, rootKey)
            customCreatePreferences()
            //eventMapping()
        }

        private fun customCreatePreferences() {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            /**
             * 기본 입력값 설정
             */
            val inputOptionCategory = PreferenceCategory(context).apply {
                title = "기본 입력값 설정"
                isIconSpaceReserved = false
            }
            screen.addPreference(inputOptionCategory)

            // 부양가족수
            val familyPreference = EditTextPreference(context).apply {
                key = Services.AppPrefKeys.DefaultInput.family
                title = "부양가족수"
                setDefaultValue("1")
                isIconSpaceReserved = false
                summaryProvider = summaryProviderPerson
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))
                }
            }
            inputOptionCategory.addPreference(familyPreference)

            // 20세 이하 자녀수
            val childPreference = EditTextPreference(context).apply {
                key = Services.AppPrefKeys.DefaultInput.child
                title = "20세 이하 자녀수"
                setDefaultValue("0")
                isIconSpaceReserved = false
                summaryProvider = summaryProviderPerson
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99))
                }
            }
            inputOptionCategory.addPreference(childPreference)

            // 비과세액
            val taxFreePreference = EditTextPreference(context).apply {
                key = Services.AppPrefKeys.DefaultInput.taxFree
                title = "비과세액"
                setDefaultValue("100000")
                isIconSpaceReserved = false
                summaryProvider = summaryProviderMoney
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99999999))
                }
            }
            inputOptionCategory.addPreference(taxFreePreference)

            // 입력 초기값
            val moneyPreference = EditTextPreference(context).apply {
                key = Services.AppPrefKeys.DefaultInput.money
                title = "금액 입력 초기값"
                setDefaultValue("2000000")
                isIconSpaceReserved = false
                summaryProvider = summaryProviderMoney
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 99999999999))
                }
            }
            inputOptionCategory.addPreference(moneyPreference)
            
            
            /**
             * 세율 설정
             */
            // 세율 설정 그룹
            val rateOptionCategory = PreferenceCategory(context).apply {
                title = "세율 설정"
                isIconSpaceReserved = false
            }
            screen.addPreference(rateOptionCategory)

            // 세율 고급설정 사용 여부
            val rateOptionSwitch = SwitchPreferenceCompat(context).apply {
                key = Services.AppPrefKeys.customRateEnable
                title = "세율 고급설정 사용"
                setDefaultValue(false)
                isIconSpaceReserved = false
            }
            rateOptionCategory.addPreference(rateOptionSwitch)

            // 세율설정 변경 초기화
            val resetRates = Preference(context).apply{
                title = "세율설정 변경 초기화"
                summary = "세율설정을 원래 값으로 돌려놓습니다."
                //dependency = Services.AppPrefKeys.customRateEnable
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    resetRates()
                }
            }
            rateOptionCategory.addPreference(resetRates)

            // 국민연금 세율
            val rateNpPreference = EditTextPreference(context).apply {
                key = Services.AppPrefKeys.CustomRates.nationalPension
                title = "국민연금 세율"
                setDefaultValue("0")
                //dependency = Services.AppPrefKeys.customRateEnable
                isIconSpaceReserved = false
                summaryProvider = summaryProviderRate
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener(editTextListenerRates)
            }
            rateOptionCategory.addPreference(rateNpPreference)

            // 건강보험 세율
            val rateHcPreference = EditTextPreference(context).apply{
                key = Services.AppPrefKeys.CustomRates.healthCare
                title = "건강보험 세율"
                setDefaultValue("0")
                //dependency = Services.AppPrefKeys.customRateEnable
                isIconSpaceReserved = false
                summaryProvider = summaryProviderRate
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener(editTextListenerRates)
            }
            rateOptionCategory.addPreference(rateHcPreference)

            // 장기요양보험 세율
            val rateLcPreference = EditTextPreference(context).apply{
                key = Services.AppPrefKeys.CustomRates.longTermCare
                title = "장기요양보험 세율"
                setDefaultValue("0")
                //dependency = Services.AppPrefKeys.customRateEnable
                isIconSpaceReserved = false
                summaryProvider = summaryProviderRate
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener(editTextListenerRates)
            }
            rateOptionCategory.addPreference(rateLcPreference)

            // 고용보험 세율
            val rateEcPreference = EditTextPreference(context).apply{
                key = Services.AppPrefKeys.CustomRates.employmentCare
                title = "고용보험 세율"
                setDefaultValue("0")
                //dependency = Services.AppPrefKeys.customRateEnable
                isIconSpaceReserved = false
                summaryProvider = summaryProviderRate
                dialogTitle = title
                onPreferenceChangeListener = changeListenerSyncServicesAppPref
                setOnBindEditTextListener(editTextListenerRates)
            }
            rateOptionCategory.addPreference(rateEcPreference)

            preferenceScreen = screen


            // 연결성 설정 (뒷부분에 있어야 하는 듯...)
            rateNpPreference.dependency = Services.AppPrefKeys.customRateEnable
            rateHcPreference.dependency = Services.AppPrefKeys.customRateEnable
            rateLcPreference.dependency = Services.AppPrefKeys.customRateEnable
            rateEcPreference.dependency = Services.AppPrefKeys.customRateEnable
            resetRates.dependency = Services.AppPrefKeys.customRateEnable
        }


        private val summaryProviderMoney = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (TextUtils.isEmpty(text)) {
                "0"
            } else {
                String.format("%,d 원",text.toLongOrNull())
                //"$text 원"
            }
        }

        private val summaryProviderPerson = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (TextUtils.isEmpty(text)) {
                "0"
            } else {
                "$text 명"
            }
        }

        private val summaryProviderRate = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (TextUtils.isEmpty(text)) {
                "0"
            } else {
                "$text %"
            }
        }

        /**
         * 세율의 입력에 관한 리스터.
         */
        private val editTextListenerRates = EditTextPreference.OnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
        }

        /**
         * Services.appPrefs 에도 값을 대입해둔다. (디버깅이 편하고 사용이 편하려고)
         */
        private val changeListenerSyncServicesAppPref = Preference.OnPreferenceChangeListener { pref, newValue ->
            Services.setAppPref(pref.key, newValue)
            true
        }

        /**
         * 세율 설정을 초기화.
         */
        private fun resetRates(): Boolean{
            // PreferenceManager.getDefaultSharedPreferences(activity)

            // rates
            val rNationalPension: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.nationalPension)
            val rHealthCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.healthCare)
            val rLongTermCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.longTermCare)
            val rEmploymentCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.employmentCare)

            rNationalPension?.text = Services.DefaultRates.nationalPension.toString()
            rHealthCare?.text = Services.DefaultRates.healthCare.toString()
            rLongTermCare?.text = Services.DefaultRates.longTermCare.toString()
            rEmploymentCare?.text = Services.DefaultRates.employmentCare.toString()

            return true
        }
    }

}