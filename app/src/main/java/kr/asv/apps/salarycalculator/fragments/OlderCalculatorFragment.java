package kr.asv.apps.salarycalculator.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import kr.asv.apps.salarycalculator.MoneyTextWatcher;
import kr.asv.apps.salarycalculator.activities.ReportActivity;
import kr.asv.apps.salarycalculator.Services;
import kr.asv.calculators.salary.SalaryCalculator;
import kr.asv.shhtaxmanager.R;

/**
 * create an instance of this fragment.
 */
public class OlderCalculatorFragment extends BaseFragment {
	@SuppressWarnings("FieldCanBeLocal")
	private boolean includedSeverance = false;
	private boolean annualBasis = false;

	public OlderCalculatorFragment() {
		// Required empty public constructor
	}

	/**
	 */
	@SuppressWarnings("unused")
	public static OlderCalculatorFragment newInstance() {
		OlderCalculatorFragment fragment = new OlderCalculatorFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_older_calculator, container, false);
		setFragmentView(view);
		//setActionBarTitle("실수령액 계산");
		setActionBarTitle(getResources().getString(R.string.activity_title_normal_mode));

		//세자리마다 쉼표표시
		EditText edMoney = (EditText) findViewById(R.id.edMoney);
		EditText edOptionTaxFree = (EditText) findViewById(R.id.edOptionTaxFree);
		edMoney.addTextChangedListener(new MoneyTextWatcher(edMoney));
		edOptionTaxFree.addTextChangedListener(new MoneyTextWatcher(edOptionTaxFree));
		return view;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initEventListener();
	}

	private void initEventListener() {
		//debug("[Older]이벤트 리스너 초기화");

		// 계산하기 버튼 클릭시
		findViewById(R.id.btnCalculate).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickButtonCalculate();// 계산하기 버튼 클릭시
			}
		});

		// 연봉선택시
		findViewById(R.id.raMoneyYearly).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMoneyType(v);
			}
		});

		// 월급선택시
		findViewById(R.id.raMoneyMonthly).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickMoneyType(v);
			}
		});

        /*
        // 퇴직금여부 선택시
        Switch swOpSeverance = (Switch) findViewById(R.id.swYearlyOpSeverance);
        swOpSeverance.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                includedSeverance = isChecked;
            }
        });
        */
	}

	/**
	 * 월급/연봉 선택
	 *
	 * @param v View
	 */
	private void onClickMoneyType(View v) {
		RadioButton radioButton = (RadioButton) v;
		TextView txMoneyLabel = (TextView) findViewById(R.id.titleMoneyLabel);
		LinearLayout layYearlyOpSeverance = (LinearLayout) findViewById(R.id.divYearlyOpSeverance);

		//debug("");
		if (radioButton.isChecked()) {
			if (radioButton.getId() == R.id.raMoneyMonthly) {
				this.annualBasis = false;
				txMoneyLabel.setText("월급입력");
				layYearlyOpSeverance.setVisibility(View.INVISIBLE);
			} else {
				this.annualBasis = true;
				txMoneyLabel.setText("연봉입력");
				layYearlyOpSeverance.setVisibility(View.VISIBLE);
			}
			//debug("체크시 - 연봉여부["+this.annualBasis+"]");
		}
	}

	/**
	 * 세금 계산하기 버튼 클릭시 발생
	 */
	private void onClickButtonCalculate() {
		EditText edMoney = (EditText) findViewById(R.id.edMoney);
		EditText edOptionTaxFree = (EditText) findViewById(R.id.edOptionTaxFree);
		EditText edOptionFamily = (EditText) findViewById(R.id.edOptionFamily);
		EditText edOptionChild = (EditText) findViewById(R.id.edOptionChild);

		// getMoney
		long inputMoney;
		if (edMoney.getText().length() <= 1) {
			return;
		}
		try {
			//inputMoney = Integer.parseInt(edMoney.getText().toString());
			inputMoney = MoneyTextWatcher.getValue(edMoney);
		} catch (Exception e) {
			//debug("인트 변환 에러");
			inputMoney = 0;
		}
		// getTaxFree
		long taxExemption;
		if (edOptionTaxFree.getText().toString().length() <= 1) {
			edOptionTaxFree.setText("0");
		}
		//taxExemption = Integer.parseInt(edOptionTaxFree.getText().toString());
		taxExemption = MoneyTextWatcher.getValue(edOptionTaxFree);

		// getFamily
		if (edOptionFamily.getText().toString().length() <= 1) {
			edOptionFamily.setText("1");
		}
		int family = Integer.parseInt(edOptionFamily.getText().toString());

		// getChild
		int child;
		if (edOptionChild.getText().toString().length() <= 1) {
			edOptionChild.setText("0");
		}
		child = Integer.parseInt(edOptionChild.getText().toString());

		RadioGroup moneyType = (RadioGroup) findViewById(R.id.rgMoneyType);
		//noinspection RedundantIfStatement
		if (R.id.raMoneyYearly == moneyType.getCheckedRadioButtonId()) {
			annualBasis = true;
		} else {
			annualBasis = false;
		}

        /*
        Switch swSeverance = (Switch) findViewById(R.id.swYearlyOpSeverance);
        includedSeverance = swSeverance.isChecked();
        */

		CheckBox checkSeverance = (CheckBox) findViewById(R.id.checkSeverance);
		includedSeverance = checkSeverance.isChecked();
		//checkSeverance


		// 연산
		SalaryCalculator calculator = Services.getInstance().getCalculator();

		//옵션값 셋팅
		//CalculatorOptions options = ;
		calculator.getOptions().setInputMoney(inputMoney);
		calculator.getOptions().setTaxExemption(taxExemption);
		calculator.getOptions().setFamily(family);
		calculator.getOptions().setChild(child);
		calculator.getOptions().setAnnualBasis(annualBasis);
		calculator.getOptions().setIncludedSeverance(includedSeverance);

		calculator.run();

		//결과 화면 호출
		Intent intent = new Intent(getActivity(), ReportActivity.class);
		startActivity(intent);
	}
}
