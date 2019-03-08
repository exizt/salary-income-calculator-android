package kr.asv.apps.salarycalculator.activities


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NavUtils
import android.text.InputFilter
import android.view.MenuItem
import kr.asv.androidutils.inputfilter.InputFilterDoubleMinMax
import kr.asv.apps.salarycalculator.AppCompatPreferenceActivity
import kr.asv.apps.salarycalculator.BuildConfig
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.R
import org.jetbrains.anko.defaultSharedPreferences

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
                || QuickCalculatePreferenceFragment::class.java.name == fragmentName
                || AppAboutPreferenceFragment::class.java.name == fragmentName)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AdvanceTaxRatesPreferenceFragment : PreferenceFragment() {
        private var nationalPensionCustomRatePrefKey = ""
        private var healthCareCustomRatePrefKey = ""
        private var longTermCareCustomRatePrefKey = ""
        private var employmentCareCustomRatePrefKey = ""

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_advance_taxrates)
            setHasOptionsMenu(true)


            nationalPensionCustomRatePrefKey = resources.getString(R.string.pref_key_custom_national_pension_rate)
            healthCareCustomRatePrefKey = resources.getString(R.string.pref_key_custom_health_care_rate)
            longTermCareCustomRatePrefKey = resources.getString(R.string.pref_key_custom_long_term_care_rate)
            employmentCareCustomRatePrefKey = resources.getString(R.string.pref_key_custom_employment_care_rate)

            // 에디트 필터 설정
            (findPreference(nationalPensionCustomRatePrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(healthCareCustomRatePrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(longTermCareCustomRatePrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(employmentCareCustomRatePrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))

            // 이벤트 바인딩
            bindPreferenceSummaryToValue(findPreference(nationalPensionCustomRatePrefKey))
            bindPreferenceSummaryToValue(findPreference(healthCareCustomRatePrefKey))
            bindPreferenceSummaryToValue(findPreference(longTermCareCustomRatePrefKey))
            bindPreferenceSummaryToValue(findPreference(employmentCareCustomRatePrefKey))

            // 기본값 설정
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if(! prefs.contains(nationalPensionCustomRatePrefKey)){
                resetRates(prefs)
            }

            //초기화 버튼
            val prefResetButton = findPreference("rate_initialize_button")
            prefResetButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                resetRates(prefs)
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
         * @todo 기존에는 InsuranceRates 의 default 값을 가져오는 방식이었으나, preferences 를 이용하는 방식으로 교체해야 한다.
         *
         */
        private fun resetRates(prefs : SharedPreferences) {
            // 초기화 값을 가져와서 셋팅한다.
            setTextWithSummary(nationalPensionCustomRatePrefKey, prefs.getString( Services.DefaultRatesPrefKey.nationalPension,""))
            setTextWithSummary(healthCareCustomRatePrefKey, prefs.getString( Services.DefaultRatesPrefKey.healthCare,""))
            setTextWithSummary(longTermCareCustomRatePrefKey, prefs.getString( Services.DefaultRatesPrefKey.longTermCare,""))
            setTextWithSummary(employmentCareCustomRatePrefKey, prefs.getString( Services.DefaultRatesPrefKey.employmentCare,""))
        }

        /**
         * setText 만 하면 값만 바뀌고, Summary 부분이 바뀌지 않으므로. 아래와 같은 구문을 사용.
         * 결과적으로, view 의 text 변경되고 preference 도 변경됨.
         * @param key   String
         * @param value String
         */
        private fun setTextWithSummary(key: CharSequence, value: String) {
            val preference = findPreference(key) as EditTextPreference
            preference.text = value
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value)
        }
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

    /**
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AppAboutPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_about)
            setHasOptionsMenu(true)

            //findPreference("quick_settings_family")
            //(findPreference(nationalPensionCustomRatePrefKey) as EditTextPreference).editText
            (findPreference("app_version") as Preference).summary = "v"+ BuildConfig.VERSION_NAME
            (findPreference("app_version_code") as Preference).summary = "Number "+ BuildConfig.VERSION_CODE.toString()
            (findPreference("main_database_version") as Preference).summary = defaultSharedPreferences.getInt("DB_CURRENT_VERSION",0).toString()
            (findPreference("main_database_version") as Preference).summary = defaultSharedPreferences.getInt("DB_CURRENT_VERSION",0).toString()

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

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         * @todo 하드 코딩된 부분이 있어서, 이 부분을 처리할 아이디어가 필요함.
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
         * 주의) 이벤트도 바인딩하는 메서드임. onCreate 같은 곳에서 호출해야 함.
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
