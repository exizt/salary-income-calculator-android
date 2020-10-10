package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
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

        private fun eventMapping(){
            // quick
            val qMoneyBase: EditTextPreference? = findPreference(getString(R.string.pref_key_quick_input_criteria))
            val qFamily: EditTextPreference? = findPreference(getString(R.string.pref_key_quick_family))
            val qChild: EditTextPreference? = findPreference(getString(R.string.pref_key_quick_child))
            val qExemption: EditTextPreference? = findPreference(getString(R.string.pref_key_quick_tax_exemption))

            // summary 바인딩
            qMoneyBase?.summaryProvider = summaryProviderMoney
            qFamily?.summaryProvider = summaryProviderPerson
            qChild?.summaryProvider = summaryProviderPerson
            qExemption?.summaryProvider = summaryProviderMoney

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
            val rNationalPension: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_national_pension_rate))
            val rHealthCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_health_care_rate))
            val rLongTermCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_long_term_care_rate))
            val rEmploymentCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_employment_care_rate))

            // summary 바인딩
            rNationalPension?.summaryProvider = summaryProviderRate
            rHealthCare?.summaryProvider = summaryProviderRate
            rLongTermCare?.summaryProvider = summaryProviderRate
            rEmploymentCare?.summaryProvider = summaryProviderRate

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
         * 세율 설정을 초기화.
         */
        private fun resetRates(): Boolean{
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

            // rates
            val rNationalPension: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_national_pension_rate))
            val rHealthCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_health_care_rate))
            val rLongTermCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_long_term_care_rate))
            val rEmploymentCare: EditTextPreference? = findPreference(getString(R.string.pref_key_custom_employment_care_rate))

            rNationalPension?.text = prefs.getString( Services.DefaultRatesPrefKey.nationalPension,"")
            rHealthCare?.text = prefs.getString( Services.DefaultRatesPrefKey.healthCare,"")
            rLongTermCare?.text = prefs.getString( Services.DefaultRatesPrefKey.longTermCare,"")
            rEmploymentCare?.text = prefs.getString( Services.DefaultRatesPrefKey.employmentCare,"")

            return true
        }
    }

}