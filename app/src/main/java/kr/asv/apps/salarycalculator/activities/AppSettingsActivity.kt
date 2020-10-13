package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kr.asv.androidutils.inputfilter.InputFilterDoubleMinMax
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
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            eventMapping()
        }

        // @todo 퇴직금 포함 여부 빠짐.
        private fun eventMapping(){
            // quick
            val qMoneyBase: EditTextPreference? = findPreference(Services.AppPrefKeys.inputBase)
            val qFamily: EditTextPreference? = findPreference(Services.AppPrefKeys.DefaultInput.family)
            val qChild: EditTextPreference? = findPreference(Services.AppPrefKeys.DefaultInput.child)
            val qExemption: EditTextPreference? = findPreference(Services.AppPrefKeys.DefaultInput.taxExemption)

            // summary 바인딩
            qMoneyBase?.summaryProvider = summaryProviderMoney
            qFamily?.summaryProvider = summaryProviderPerson
            qChild?.summaryProvider = summaryProviderPerson
            qExemption?.summaryProvider = summaryProviderMoney

            // change 바인딩
            qMoneyBase?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            qFamily?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            qChild?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            qExemption?.onPreferenceChangeListener = changeListenerSyncServicesAppPref

            // 입력값 필터
            qMoneyBase?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99999999))
            }

            qFamily?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))
            }
            qChild?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))
            }
            qExemption?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99999999))
            }

            // rates
            val rNationalPension: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.nationalPension)
            val rHealthCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.healthCare)
            val rLongTermCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.longTermCare)
            val rEmploymentCare: EditTextPreference? = findPreference(Services.AppPrefKeys.CustomRates.employmentCare)

            // summary 바인딩
            rNationalPension?.summaryProvider = summaryProviderRate
            rHealthCare?.summaryProvider = summaryProviderRate
            rLongTermCare?.summaryProvider = summaryProviderRate
            rEmploymentCare?.summaryProvider = summaryProviderRate

            // change 바인딩
            rNationalPension?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            rHealthCare?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            rLongTermCare?.onPreferenceChangeListener = changeListenerSyncServicesAppPref
            rEmploymentCare?.onPreferenceChangeListener = changeListenerSyncServicesAppPref

            // 입력값 필터
            rNationalPension?.setOnBindEditTextListener(editTextListenerRates)
            rHealthCare?.setOnBindEditTextListener(editTextListenerRates)
            rLongTermCare?.setOnBindEditTextListener(editTextListenerRates)
            rEmploymentCare?.setOnBindEditTextListener(editTextListenerRates)

            val rateResetButton: Preference? = findPreference("rate_reset_button")
            rateResetButton?.setOnPreferenceClickListener {
                resetRates()
            }
        }

        private val summaryProviderMoney = Preference.SummaryProvider<EditTextPreference> { preference ->
            val text = preference.text
            if (TextUtils.isEmpty(text)) {
                "0"
            } else {
                "$text 원"
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

        private val editTextListenerRates = EditTextPreference.OnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
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