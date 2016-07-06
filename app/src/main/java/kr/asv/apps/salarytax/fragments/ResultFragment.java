package kr.asv.apps.salarytax.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import kr.asv.apps.salarytax.Services;
import kr.asv.calculators.salary.SalaryCalculator;
import kr.asv.shhtaxmanager.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        setFragmentView(view);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEventListener();
        showResult();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void initEventListener()
    {

        // 닫기 버튼 클릭시
        findViewById(R.id.btnClose).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // ResultFragment fragment = new ResultFragment();
                // FragmentManager fragmentManager = getFragmentManager();
                // fragmentManager.beginTransaction().replace(R.id.container,
                // fragment).commit();
                // getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                closeFragment();
            }
        });

    }
    private void closeFragment() {
        // getActivity().onBackPressed();
        // getActivity().getFragmentManager().beginTransaction().remove(this);
        getActivity().getSupportFragmentManager().popBackStack();

    }
    private void showResult()
    {
        hideSoftKeyboard();
        //MainActivity activity = (MainActivity)getActivity();
        //Calculator calculator = activity.getCalculator();
        SalaryCalculator calculator = Services.getInstance().getCalculator();
        //debug(calculator.getOptions().toString());

        TextView txNation = (TextView) findViewById(R.id.txInsuranceNation);
        TextView txInsuranceEmp = (TextView) findViewById(R.id.txInsuranceEmp);
        TextView txInsuranceHealth = (TextView) findViewById(R.id.txInsuranceHealth);
        TextView txInsuranceLongterm = (TextView) findViewById(R.id.txInsuranceLongterm);
        TextView txTaxEarned = (TextView) findViewById(R.id.txTaxEarned);
        TextView txTaxEarnedLocal = (TextView) findViewById(R.id.txTaxEarnedLocal);
        TextView txNetSalary = (TextView) findViewById(R.id.txNetSalary);
        TextView txNetSalaryY = (TextView) findViewById(R.id.txNetSalaryY);
        TextView txViewSalary = (TextView) findViewById(R.id.txViewSalary);
        TextView txViewSalaryY = (TextView) findViewById(R.id.txViewSalaryY);

        DecimalFormat format = new java.text.DecimalFormat("###,##0");

        // 국민연금
        double nation = calculator.getInsurance().getNationalPension();
        txNation.setText(format.format(nation));
        txNation.append(" 원");

        // 건강보험(건강보험)
        double health = calculator.getInsurance().getHealthCare();
        txInsuranceHealth.setText(format.format(health));
        txInsuranceHealth.append(" 원");

        // 건강보험(요양보험)
        double longterm = calculator.getInsurance().getLongTermCare();
        txInsuranceLongterm.setText(format.format(longterm));
        txInsuranceLongterm.append(" 원");

        // 고용보험
        double employee = calculator.getInsurance().getEmploymentCare();
        txInsuranceEmp.setText(format.format(employee));
        txInsuranceEmp.append(" 원");

        // 소득세
        double taxEarned = calculator.getIncomeTax().getEarnedIncomeTax();
        txTaxEarned.setText(format.format(taxEarned));
        txTaxEarned.append(" 원");

        // 지방소득세
        double taxEarnedLocal = calculator.getIncomeTax().getLocalTax();
        txTaxEarnedLocal.setText(format.format(taxEarnedLocal));
        txTaxEarnedLocal.append(" 원");

        // 실수령액. 연봉 및 월급
        double netSalary = calculator.getNetSalary();
        txNetSalary.setText(format.format(netSalary));
        txNetSalary.append(" 원");
        double netSalaryY = calculator.getNetAnnualSalary();
        txNetSalaryY.setText(format.format(netSalaryY));
        txNetSalaryY.append(" 원");

        // 계산된 기준 연봉 및 월급
        double viewSalary = calculator.getSalary().getGrossSalary();
        txViewSalary.setText(format.format(viewSalary));
        txViewSalary.append(" 원");
        double viewSalaryY = calculator.getSalary().getGrossAnnualSalary();
        txViewSalaryY.setText(format.format(viewSalaryY));
        txViewSalaryY.append(" 원");

    }

}
