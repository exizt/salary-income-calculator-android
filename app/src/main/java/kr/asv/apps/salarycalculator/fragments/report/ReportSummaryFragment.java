package kr.asv.apps.salarycalculator.fragments.report;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import kr.asv.apps.salarycalculator.Services;
import kr.asv.apps.salarycalculator.fragments.BaseFragment;
import kr.asv.calculators.salary.SalaryCalculator;
import kr.asv.shhtaxmanager.R;

/**
 */
public class ReportSummaryFragment extends BaseFragment {
	public ReportSummaryFragment() {
		// Required empty public constructor
	}


	public static ReportSummaryFragment newInstance() {
		ReportSummaryFragment fragment = new ReportSummaryFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_report_summary, container, false);
		setFragmentView(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showResult();
	}

	private void showResult() {
		//hideSoftKeyboard();
		SalaryCalculator calculator = Services.getInstance().getCalculator();
		DecimalFormat format = new java.text.DecimalFormat("###,##0");

		//실 수령액
		TextView txNetSalary = (TextView) findViewById(R.id.txNetSalary);
		txNetSalary.setText(format.format(calculator.getNetSalary()));
		txNetSalary.append(" 원");

		//4대보험+세금합계
		TextView txMinusTotal = (TextView) findViewById(R.id.txMinusTotal);
		double minusTotal = calculator.getInsurance().get() + calculator.getIncomeTax().get();
		txMinusTotal.setText(format.format(minusTotal));
		txMinusTotal.append(" 원");

	}
}
