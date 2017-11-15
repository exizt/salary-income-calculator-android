package kr.asv.apps.salarycalculator.fragments.report;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
public class ReportInputFragment extends BaseFragment {
	public ReportInputFragment() {
		// Required empty public constructor
	}

	public static ReportInputFragment newInstance() {
		ReportInputFragment fragment = new ReportInputFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_input, container, false);
		setFragmentView(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showResult();
	}

	@SuppressLint("DefaultLocale")
	private void showResult() {
		SalaryCalculator calculator = Services.getInstance().getCalculator();

		DecimalFormat format = new java.text.DecimalFormat("###,##0");

		//계산된 연봉
		TextView txInputSalaryAnnual = (TextView) findViewById(R.id.txInputSalaryAnnual);
		txInputSalaryAnnual.setText(format.format(calculator.getSalary().getGrossAnnualSalary()));
		txInputSalaryAnnual.append(" 원");

		//계산된 월급
		TextView txInputSalary = (TextView) findViewById(R.id.txInputSalary);
		txInputSalary.setText(format.format(calculator.getSalary().getGrossSalary()));
		txInputSalary.append(" 원");

		//부양가족수
		TextView txInputFamily = (TextView) findViewById(R.id.txInputFamily);
		txInputFamily.setText(String.format("%d 명", calculator.getOptions().getFamily()));

		//20세이하자녀수
		TextView txInputChild = (TextView) findViewById(R.id.txInputChild);
		txInputChild.setText(String.format("%d 명", calculator.getOptions().getChild()));

		//입력한 비과세액
		TextView txInputTaxExemption = (TextView) findViewById(R.id.txInputTaxExemption);
		txInputTaxExemption.setText(format.format(calculator.getOptions().getTaxExemption()));
		txInputTaxExemption.append(" 원");
	}
}
