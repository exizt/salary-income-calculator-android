package kr.asv.apps.salarytax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import kr.asv.apps.salarytax.Services;
import kr.asv.calculators.salary.SalaryCalculator;
import kr.asv.shhtaxmanager.R;

public class ReportInsuranceFragment extends BaseFragment {
    public ReportInsuranceFragment() {
    }

    public static ReportInsuranceFragment newInstance() {
        ReportInsuranceFragment fragment = new ReportInsuranceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_insurance, container, false);
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

        //국민연금
        TextView txNationPension = (TextView) findViewById(R.id.txNationPension);
        txNationPension.setText(format.format(calculator.getInsurance().getNationalPension()));
        txNationPension.append(" 원");

        //건강보험료
        TextView txHealthCare = (TextView) findViewById(R.id.txHealthCare);
        txHealthCare.setText(format.format(calculator.getInsurance().getHealthCare()));
        txHealthCare.append(" 원");

        //요양보험료
        TextView txLongtermCare = (TextView) findViewById(R.id.txLongtermCare);
        txLongtermCare.setText(format.format(calculator.getInsurance().getLongTermCare()));
        txLongtermCare.append(" 원");

        //고용보험
        TextView txEmploymentCare = (TextView) findViewById(R.id.txEmploymentCare);
        txEmploymentCare.setText(format.format(calculator.getInsurance().getEmploymentCare()));
        txEmploymentCare.append(" 원");

    }
}
