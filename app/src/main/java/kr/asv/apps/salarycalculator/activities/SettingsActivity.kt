package kr.asv.apps.salarycalculator.activities


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NavUtils
import android.text.InputFilter
import android.text.Spanned
import android.view.MenuItem
import kr.asv.apps.salarycalculator.AppCompatPreferenceActivity
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.R

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean = isXLargeTablet(this)

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return (PreferenceFragment::class.java.name == fragmentName
                || AdvanceTaxRatesPreferenceFragment::class.java.name == fragmentName
                || QuickCalculatePreferenceFragment::class.java.name == fragmentName)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AdvanceTaxRatesPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_advance_taxrates)
            setHasOptionsMenu(true)

            //setDefaultRates();

            (findPreference("rate_national_pension") as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference("rate_health_care") as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference("rate_longterm_care") as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference("rate_employment_care") as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))

            bindPreferenceSummaryToValue(findPreference("rate_national_pension"))
            bindPreferenceSummaryToValue(findPreference("rate_health_care"))
            bindPreferenceSummaryToValue(findPreference("rate_longterm_care"))
            bindPreferenceSummaryToValue(findPreference("rate_employment_care"))

            //초기화 버튼
            val prefResetButton = findPreference("rate_initialize_button")
            prefResetButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetRates()
                false
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        /**
         * Calculator 클래스를 통해서, Rate 기본설정 및, 기본설정값을 가져오고, 기본값을 배치한다.
         */
        private fun resetRates() {
            //Calculator 클래스에서 Rate 기본값으로 설정하면서 기본값을 가져온다.
            Services.calculator.insurance.rates.initValues()
            val defaultNationalPensionRate = Services.calculator.insurance.rates.nationalPension
            val defaultHealthCareRate = Services.calculator.insurance.rates.healthCare
            val defaultLongTermCareRate = Services.calculator.insurance.rates.longTermCare
            val defaultEmploymentCareRate = Services.calculator.insurance.rates.employmentCare

            setTextWithSummary("rate_national_pension", defaultNationalPensionRate.toString())
            setTextWithSummary("rate_health_care", defaultHealthCareRate.toString())
            setTextWithSummary("rate_longterm_care", defaultLongTermCareRate.toString())
            setTextWithSummary("rate_employment_care", defaultEmploymentCareRate.toString())
        }

        /**
         * setText 만 하면 값만 바뀌고, Summary 부분이 바뀌지 않으므로. 아래와 같은 구문을 사용.
         *
         * @param key   String
         * @param value String
         */
        private fun setTextWithSummary(key: CharSequence, value: String) {
            val preference = findPreference(key) as EditTextPreference
            preference.text = value
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value)
        }
        /*
        public void setDefaultRates()
        {
            InsuranceRates initInsuranceRates = new InsuranceRates();
            Double defaultNationalPensionRate = initInsuranceRates.getNationalPension();
            Double defaultHealthCareRate = initInsuranceRates.getHealthCare();
            Double defaultLongTermCareRate = initInsuranceRates.getLongTermCare();
            Double defaultEmploymentCareRate = initInsuranceRates.getEmploymentCare();

            setDefaultValue("rate_national_pension",String.valueOf(defaultNationalPensionRate));
            setDefaultValue("rate_health_care",String.valueOf(defaultHealthCareRate));
            setDefaultValue("rate_longterm_care",String.valueOf(defaultLongTermCareRate));
        }
        public void setDefaultValue(CharSequence key,String value)
        {
            EditTextPreference preference = ((EditTextPreference)findPreference(key));
            preference.setDefaultValue(value);
        }
        */
    }

    /**
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class QuickCalculatePreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_quickcalculate)
            setHasOptionsMenu(true)


            bindPreferenceSummaryToValue(findPreference("quick_settings_family"))
            bindPreferenceSummaryToValue(findPreference("quick_settings_child"))
            bindPreferenceSummaryToValue(findPreference("quick_settings_tax_exemption"))


        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    class InputFilterDoubleMinMax : InputFilter {
        private var min = 0.0
        private var max: Double = 0.toDouble()

        internal constructor(min: Int, max: Int) {
            this.min = min.toDouble()
            this.max = max.toDouble()
        }

        @Suppress("unused")
        constructor(min: Double, max: Double) {
            this.min = min
            this.max = max
        }

        @Suppress("unused")
        constructor(min: String, max: String) {
            this.min = java.lang.Double.parseDouble(min)
            this.max = java.lang.Double.parseDouble(max)
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, _start: Int, _end: Int): CharSequence? {
            try {
                val input = java.lang.Double.parseDouble(dest.toString() + source.toString())
                if (isInRange(min, max, input))
                    return null
            } catch (ignored: NumberFormatException) {
            }

            return ""
        }

        private fun isInRange(a: Double, b: Double, c: Double): Boolean {
            return if (b > a) {
                c in a..b
            } else {
                c in b..a
            }
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            var stringValue = value.toString()

            when (preference) {
                is ListPreference -> {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    val index = preference.findIndexOfValue(stringValue)

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            if (index >= 0)
                                preference.entries[index]
                            else
                                null)

                }
                is EditTextPreference -> {
                    val key = preference.getKey()
                    when (key) {
                        "rate_national_pension", "rate_health_care", "rate_longterm_care", "rate_employment_care" -> {
                            if (java.lang.Float.parseFloat(stringValue) >= 100) {
                                stringValue = "100"
                            }
                            preference.setSummary("$stringValue %")
                        }
                        "quick_settings_tax_exemption" -> preference.setSummary("$stringValue 원")
                        "quick_settings_family", "quick_settings_child" -> preference.setSummary("$stringValue 명")
                        else -> preference.setSummary(stringValue)
                    }
                }
                else -> preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean =
                context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
