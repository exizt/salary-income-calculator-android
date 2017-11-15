package kr.asv.apps.salarytax.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import kr.asv.apps.salarytax.MoneyTextWatcher;
import kr.asv.apps.salarytax.activities.ReportActivity;
import kr.asv.apps.salarytax.Services;
import kr.asv.calculators.salary.SalaryCalculator;
import kr.asv.shhtaxmanager.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuickCalculatorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuickCalculatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class QuickCalculatorFragment extends BaseFragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuickCalculatorFragment.
     */
    public static QuickCalculatorFragment newInstance(String param1, String param2) {
        QuickCalculatorFragment fragment = new QuickCalculatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public QuickCalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_calculator, container, false);
        setFragmentView(view);
        setActionBarTitle(getResources().getString(R.string.activity_title_quick_mode));
        EditText editMoney = (EditText) findViewById(R.id.editMoney_QMode);
        editMoney.addTextChangedListener(new MoneyTextWatcher(editMoney));

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEventListener();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void initEventListener()
    {
        // 계산하기 버튼 클릭시
        findViewById(R.id.btnExecute_QMode).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonCalculate(v);// 계산하기 버튼 클릭시
            }
        });

        // 금액 추가 버튼 +천만
        findViewById(R.id.btnPlus1000).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputMoney(10000000);
            }
        });

        // 금액 추가 버튼 +백만
        findViewById(R.id.btnPlus100).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputMoney(1000000);
            }
        });

        // 금액 추가 버튼 +십만
        findViewById(R.id.btnPlus10).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputMoney(100000);
            }
        });

        // 금액 감소 버튼 -천만
        findViewById(R.id.btnMinus1000).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusInputMoney(10000000);
            }
        });

        // 금액 감소 버튼 -백만
        findViewById(R.id.btnMinus100).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusInputMoney(1000000);
            }
        });

        // 금액 감소 버튼 - 십만
        findViewById(R.id.btnMinus10).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusInputMoney(100000);
            }
        });

        // 금액 정정 버튼
        findViewById(R.id.btnClearInput_QM).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInputMoney();
            }
        });
    }
    public void initInputMoney()
    {
        EditText editInputMoney = (EditText) findViewById(R.id.editMoney_QMode);
        editInputMoney.setText("0");
    }
    public void minusInputMoney(int value)
    {
        EditText editInputMoney = (EditText) findViewById(R.id.editMoney_QMode);
        //long inputMoney = getValueEditText(R.id.editMoney_QMode);
        long inputMoney = MoneyTextWatcher.getValue(editInputMoney);
        /*
        if (editInputMoney.getText().length() <= 1) {
            inputMoney = 0;
        } else {
            inputMoney = Integer.parseInt(editInputMoney.getText().toString());
        }
        */
        inputMoney -= value;

        if(inputMoney < 0) inputMoney = 0;
        editInputMoney.setText(String.valueOf(inputMoney));
    }
    public void addInputMoney(int value)
    {
        EditText editInputMoney = (EditText) findViewById(R.id.editMoney_QMode);
        //long inputMoney = getValueEditText(R.id.editMoney_QMode);
        long inputMoney = MoneyTextWatcher.getValue(editInputMoney);
        /*
        if (editInputMoney.getText().length() <= 1) {
            inputMoney = 0;
        } else {
            inputMoney = Integer.parseInt(editInputMoney.getText().toString());
        }
        */
        inputMoney += value;
        editInputMoney.setText(String.valueOf(inputMoney));
    }
    public void onClickButtonCalculate(View v) {
        EditText editInputMoney = (EditText) findViewById(R.id.editMoney_QMode);
        // getMoney
        long inputMoney = 0;
        if (editInputMoney.getText().length() <= 1) {
            return;
        }
        try {
            //inputMoney = getValueEditText(R.id.editMoney_QMode);
            inputMoney = MoneyTextWatcher.getValue(editInputMoney);
        } catch (Exception e) {
            //debug("인트 변환 에러");
            inputMoney = 0;
        }

        /*
         * 연봉기준인지 월급기준인지 구분.
         * 1000만원 이상이면 연봉입력으로 생각하고 계산. (설마 월급이 천만원은 아니겠지)
         */
        boolean annualBasis = false;
        if(inputMoney >= 10000000){
            annualBasis = true;
        }

        //옵션의 기본값
        // 비과세
        long taxExemption = 100000;
        // 부양가족수
        int family = 1;
        // 20세 이하 자녀수
        int child = 0;
        // 퇴직금 포함인지
        boolean includedSeverance = false;


        //환경설정 값 가져오기.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //[퀵계산 설정 사용] 일 때 세부옵션들을 불러온다.
        if(pref.getBoolean("quick_settings_enable",false)){
            family = Integer.parseInt(pref.getString("quick_settings_family","default"));
            child = Integer.parseInt(pref.getString("quick_settings_child","0"));
            taxExemption = Integer.parseInt(pref.getString("quick_settings_tax_exemption","100000"));
            includedSeverance = pref.getBoolean("quick_settings_severance",false);
        }

        SalaryCalculator calculator = Services.getInstance().getCalculator();

        //옵션값 셋팅
        calculator.getOptions().setInputMoney(inputMoney);
        calculator.getOptions().setTaxExemption(taxExemption);
        calculator.getOptions().setFamily(family);
        calculator.getOptions().setChild(child);
        calculator.getOptions().setAnnualBasis(annualBasis);
        calculator.getOptions().setIncludedSeverance(includedSeverance);

        if(pref.getBoolean("rate_settings_enable",false)){
            calculator.getInsurance().getRates().setNationalPension(Double.parseDouble(pref.getString("rate_national_pension","0")));
            calculator.getInsurance().getRates().setHealthCare(Double.parseDouble(pref.getString("rate_health_care","0")));
            calculator.getInsurance().getRates().setLongTermCare(Double.parseDouble(pref.getString("rate_longterm_care","0")));
            calculator.getInsurance().getRates().setEmploymentCare(Double.parseDouble(pref.getString("rate_employment_care","0")));
            //calculator.getInsurance().getRates().setEmploymentCare();
        } else {
            calculator.getInsurance().getRates().init();
        }




        calculator.run();

        //결과 화면 호출
        Intent intent=new Intent(getActivity(),ReportActivity.class);
        startActivity(intent);
    }
}
