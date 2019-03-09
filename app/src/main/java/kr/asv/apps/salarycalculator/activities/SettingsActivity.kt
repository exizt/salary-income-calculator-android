package kr.asv.apps.salarycalculator.activities


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NavUtils
import android.text.InputFilter
import android.view.MenuItem
import kr.asv.androidutils.inputfilter.InputFilterDoubleMinMax
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.androidutils.inputfilter.InputFilterMinMax
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
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorAccent)))
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
        private var npPrefKey = ""
        private var hcPrefKey = ""
        private var ltcPrefKey = ""
        private var emcPrefKey = ""

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_advance_taxrates)
            setHasOptionsMenu(true)

            npPrefKey = resources.getString(R.string.pref_key_custom_national_pension_rate)
            hcPrefKey = resources.getString(R.string.pref_key_custom_health_care_rate)
            ltcPrefKey = resources.getString(R.string.pref_key_custom_long_term_care_rate)
            emcPrefKey = resources.getString(R.string.pref_key_custom_employment_care_rate)

            // 에디트 필터 설정
            (findPreference(npPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(hcPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(ltcPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))
            (findPreference(emcPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterDoubleMinMax(0, 100))

            // 이벤트 바인딩
            bindPreferenceSummaryToValue(findPreference(npPrefKey))
            bindPreferenceSummaryToValue(findPreference(hcPrefKey))
            bindPreferenceSummaryToValue(findPreference(ltcPrefKey))
            bindPreferenceSummaryToValue(findPreference(emcPrefKey))

            // 기본값 설정
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if(! prefs.contains(npPrefKey)){
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
         */
        private fun resetRates(prefs : SharedPreferences) {
            // 초기화 값을 가져와서 셋팅한다.
            setTextWithSummary(npPrefKey, prefs.getString( Services.DefaultRatesPrefKey.nationalPension,""))
            setTextWithSummary(hcPrefKey, prefs.getString( Services.DefaultRatesPrefKey.healthCare,""))
            setTextWithSummary(ltcPrefKey, prefs.getString( Services.DefaultRatesPrefKey.longTermCare,""))
            setTextWithSummary(emcPrefKey, prefs.getString( Services.DefaultRatesPrefKey.employmentCare,""))
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

            val familyPrefKey = getString(R.string.pref_key_quick_family)
            val childPrefKey = getString(R.string.pref_key_quick_child)
            val exemptionPrefKey = getString(R.string.pref_key_quick_tax_exemption)

            bindPreferenceSummaryToValue(findPreference(familyPrefKey))
            bindPreferenceSummaryToValue(findPreference(childPrefKey))
            bindPreferenceSummaryToValue(findPreference(exemptionPrefKey))

            (findPreference(familyPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))
            (findPreference(childPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99))
            (findPreference(exemptionPrefKey) as EditTextPreference).editText.filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, "999999999999"))
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
                    val res = preference.context.resources
                    when (key) {
                        res.getString(R.string.pref_key_custom_national_pension_rate), res.getString(R.string.pref_key_custom_health_care_rate),
                        res.getString(R.string.pref_key_custom_long_term_care_rate), res.getString(R.string.pref_key_custom_employment_care_rate) -> {
                            if (java.lang.Float.parseFloat(stringValue) >= 100) {
                                stringValue = "100"
                            }
                            preference.setSummary("$stringValue %")
                        }
                        res.getString(R.string.pref_key_quick_tax_exemption) -> preference.setSummary("$stringValue 원")
                        res.getString(R.string.pref_key_quick_family), res.getString(R.string.pref_key_quick_child) -> preference.setSummary("$stringValue 명")
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
         * preference 의 실제값이 바뀌면, 그에 대응해서 summary 의 값이 바뀌도록 하는 이벤트 리스너임.
         * 이게 동작되는 환경은, 다른 클래스나 다른 곳에서 '설정값'에 change 되어었을 때.
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
