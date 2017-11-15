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

public class ReportTaxFragment extends BaseFragment {
	public ReportTaxFragment() {
	}

	public static ReportTaxFragment newInstance() {
		ReportTaxFragment fragment = new ReportTaxFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_tax, container, false);
		setFragmentView(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showResult();
	}

	private void showResult() {
		SalaryCalculator calculator = Services.getInstance().getCalculator();

		DecimalFormat format = new java.text.DecimalFormat("###,##0");

		//소득세
		TextView txEarnedTax = (TextView) findViewById(R.id.txEarnedTax);
		txEarnedTax.setText(format.format(calculator.getIncomeTax().getEarnedIncomeTax()));
		txEarnedTax.append(" 원");

		//지방세(주민세)
		TextView txLocalTax = (TextView) findViewById(R.id.txLocalTax);
		txLocalTax.setText(format.format(calculator.getIncomeTax().getLocalTax()));
		txLocalTax.append(" 원");
	}
}
