package kr.asv.apps.salarytax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.asv.shhtaxmanager.R;
import kr.asv.apps.salarytax.Services;

/**
 */
public class TaxCalculatorReportFragment extends BaseFragment {
	public TaxCalculatorReportFragment() {
	}

	public static TaxCalculatorReportFragment newInstance() {
		TaxCalculatorReportFragment fragment = new TaxCalculatorReportFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tax_calculator_report, container, false);
		setFragmentView(view);
		return view;
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		hideSoftKeyboard();
		drawReport();
		initEventListener();
	}

	private void initEventListener() {
		// 닫기 버튼 클릭시
		findViewById(R.id.id_btn_close).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
	}

	private void drawReport() {
		double nationalRate = Services.getInstance().getTaxCalculatorRates().nationalRate;
		double healthCareRate = Services.getInstance().getTaxCalculatorRates().healthCareRate;
		double longTermCareRate = Services.getInstance().getTaxCalculatorRates().longTermCareRate;
		double employmentCareRate = Services.getInstance().getTaxCalculatorRates().employmentCareRate;

		TextView viewNationalRates = (TextView) findViewById(R.id.id_view_national);
		TextView viewHealthCare = (TextView) findViewById(R.id.id_view_healthcare);
		TextView viewLongTermCare = (TextView) findViewById(R.id.id_view_longtermcare);
		TextView viewEmploymentCare = (TextView) findViewById(R.id.id_view_employmentcare);

		viewNationalRates.setText(String.valueOf(nationalRate));
		viewHealthCare.setText(String.valueOf(healthCareRate));
		viewLongTermCare.setText(String.valueOf(longTermCareRate));
		viewEmploymentCare.setText(String.valueOf(employmentCareRate));
	}
}
