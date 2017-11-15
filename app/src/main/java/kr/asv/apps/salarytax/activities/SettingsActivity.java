package kr.asv.apps.salarytax.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.List;

import kr.asv.apps.salarytax.AppCompatPreferenceActivity;
import kr.asv.apps.salarytax.Services;
import kr.asv.shhtaxmanager.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(
						index >= 0
								? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}
			} else if (preference instanceof EditTextPreference) {
				String key = preference.getKey();
				switch (key) {
					case "rate_national_pension":
					case "rate_health_care":
					case "rate_longterm_care":
					case "rate_employment_care":
						if (Float.parseFloat(stringValue) >= 100) {
							stringValue = "100";
						}
						preference.setSummary(stringValue + " %");
						break;
					case "quick_settings_tax_exemption":
						preference.setSummary(stringValue + " 원");
						break;
					case "quick_settings_family":
					case "quick_settings_child":
						preference.setSummary(stringValue + " 명");
						break;
					default:
						preference.setSummary(stringValue);
						break;
				}
			} else {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			// Show the Up button in the action bar.
			actionBar.setDisplayHomeAsUpEnabled(true);
			//actionBar.setTitle("환경설정");
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			if (!super.onMenuItemSelected(featureId, item)) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	/**
	 * This method stops fragment injection in malicious applications.
	 * Make sure to deny any unknown fragments here.
	 */
	protected boolean isValidFragment(String fragmentName) {
		return PreferenceFragment.class.getName().equals(fragmentName)
				|| AdvanceTaxRatesPreferenceFragment.class.getName().equals(fragmentName)
				|| QuickCalculatePreferenceFragment.class.getName().equals(fragmentName);
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class AdvanceTaxRatesPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.pref_advance_taxrates);
			setHasOptionsMenu(true);

			//setDefaultRates();

			((EditTextPreference) findPreference("rate_national_pension")).getEditText().setFilters(new InputFilter[]{new InputFilterDoubleMinMax(0, 100)});
			((EditTextPreference) findPreference("rate_health_care")).getEditText().setFilters(new InputFilter[]{new InputFilterDoubleMinMax(0, 100)});
			((EditTextPreference) findPreference("rate_longterm_care")).getEditText().setFilters(new InputFilter[]{new InputFilterDoubleMinMax(0, 100)});
			((EditTextPreference) findPreference("rate_employment_care")).getEditText().setFilters(new InputFilter[]{new InputFilterDoubleMinMax(0, 100)});

			bindPreferenceSummaryToValue(findPreference("rate_national_pension"));
			bindPreferenceSummaryToValue(findPreference("rate_health_care"));
			bindPreferenceSummaryToValue(findPreference("rate_longterm_care"));
			bindPreferenceSummaryToValue(findPreference("rate_employment_care"));

			//초기화 버튼
			Preference prefResetButton = findPreference("rate_initialize_button");
			prefResetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					resetRates();
					return false;
				}
			});
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == android.R.id.home) {
				startActivity(new Intent(getActivity(), SettingsActivity.class));
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		/**
		 * Cacluator 클래스를 통해서, Rate 기본설정 및, 기본설정값을 가져오고, 기본값을 배치한다.
		 */
		public void resetRates() {
			//Calcuator 클래스에서 Rate 기본값으로 설정하면서 기본값을 가져온다.
			Services.getInstance().getCalculator().getInsurance().getRates().init();
			Double defaultNationalPensionRate = Services.getInstance().getCalculator().getInsurance().getRates().getNationalPension();
			Double defaultHealthCareRate = Services.getInstance().getCalculator().getInsurance().getRates().getHealthCare();
			Double defaultLongTermCareRate = Services.getInstance().getCalculator().getInsurance().getRates().getLongTermCare();
			Double defaultEmploymentCareRate = Services.getInstance().getCalculator().getInsurance().getRates().getEmploymentCare();

			setTextWithSummary("rate_national_pension", String.valueOf(defaultNationalPensionRate));
			setTextWithSummary("rate_health_care", String.valueOf(defaultHealthCareRate));
			setTextWithSummary("rate_longterm_care", String.valueOf(defaultLongTermCareRate));
			setTextWithSummary("rate_employment_care", String.valueOf(defaultEmploymentCareRate));
		}

		/**
		 * setText 만 하면 값만 바뀌고, Summary 부분이 바뀌지 않으므로. 아래와 같은 구문을 사용.
		 *
		 * @param key   String
		 * @param value String
		 */
		public void setTextWithSummary(CharSequence key, String value) {
			EditTextPreference preference = ((EditTextPreference) findPreference(key));
			preference.setText(value);
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
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
	public static class QuickCalculatePreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_quickcalculate);
			setHasOptionsMenu(true);


			bindPreferenceSummaryToValue(findPreference("quick_settings_family"));
			bindPreferenceSummaryToValue(findPreference("quick_settings_child"));
			bindPreferenceSummaryToValue(findPreference("quick_settings_tax_exemption"));


		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == android.R.id.home) {
				startActivity(new Intent(getActivity(), SettingsActivity.class));
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	}

	public static class InputFilterDoubleMinMax implements InputFilter {
		private double min = 0.0;
		@SuppressWarnings("CanBeFinal")
		private double max;

		@SuppressWarnings("SameParameterValue")
		InputFilterDoubleMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings("unused")
		public InputFilterDoubleMinMax(double min, double max) {
			this.min = min;
			this.max = max;
		}

		@SuppressWarnings("unused")
		public InputFilterDoubleMinMax(String min, String max) {
			this.min = Double.parseDouble(min);
			this.max = Double.parseDouble(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				Double input = Double.parseDouble(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			} catch (NumberFormatException ignored) {
			}
			return "";
		}

		private boolean isInRange(Double a, Double b, Double c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
}
