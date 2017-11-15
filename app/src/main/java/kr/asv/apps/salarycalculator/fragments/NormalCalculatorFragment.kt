package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

import kr.asv.apps.salarycalculator.MoneyTextWatcher
import kr.asv.apps.salarycalculator.activities.ReportActivity
import kr.asv.apps.salarycalculator.Services
import kr.asv.calculators.salary.SalaryCalculator
import kr.asv.shhtaxmanager.R

/**
 * create an instance of this fragment.
 */
class NormalCalculatorFragment : BaseFragment() {
	private var includedSeverance = false
	private var annualBasis = false

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater!!.inflate(R.layout.fragment_older_calculator, container, false)
		setFragmentView(view)
		//setActionBarTitle("실수령액 계산");
		setActionBarTitle(resources.getString(R.string.activity_title_normal_mode))

		//세자리마다 쉼표표시
		val edMoney = findViewById(R.id.edMoney) as EditText
		val edOptionTaxFree = findViewById(R.id.edOptionTaxFree) as EditText
		edMoney.addTextChangedListener(MoneyTextWatcher(edMoney))
		edOptionTaxFree.addTextChangedListener(MoneyTextWatcher(edOptionTaxFree))
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initEventListener()
	}

	private fun initEventListener() {
		// 계산하기 버튼 클릭시
		findViewById(R.id.btnCalculate).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				onClickButtonCalculate()// 계산하기 버튼 클릭시
			}
		})

		// 연봉선택시
		findViewById(R.id.raMoneyYearly).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				onClickMoneyType(v)
			}
		})

		// 월급선택시
		findViewById(R.id.raMoneyMonthly).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				onClickMoneyType(v)
			}
		})

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
	private fun onClickMoneyType(v: View) {
		val radioButton = v as RadioButton
		val txMoneyLabel = findViewById(R.id.titleMoneyLabel) as TextView
		val layYearlyOpSeverance = findViewById(R.id.divYearlyOpSeverance) as LinearLayout

		//debug("");
		if (radioButton.isChecked) {
			if (radioButton.id == R.id.raMoneyMonthly) {
				this.annualBasis = false
				txMoneyLabel.text = "월급입력"
				layYearlyOpSeverance.visibility = View.INVISIBLE
			} else {
				this.annualBasis = true
				txMoneyLabel.text = "연봉입력"
				layYearlyOpSeverance.visibility = View.VISIBLE
			}
			//debug("체크시 - 연봉여부["+this.annualBasis+"]");
		}
	}

	/**
	 * 세금 계산하기 버튼 클릭시 발생
	 */
	private fun onClickButtonCalculate() {
		val edMoney = findViewById(R.id.edMoney) as EditText
		val edOptionTaxFree = findViewById(R.id.edOptionTaxFree) as EditText
		val edOptionFamily = findViewById(R.id.edOptionFamily) as EditText
		val edOptionChild = findViewById(R.id.edOptionChild) as EditText

		// getMoney
		var inputMoney: Long
		if (edMoney.text.length <= 1) {
			return
		}
		try {
			//inputMoney = Integer.parseInt(edMoney.getText().toString());
			inputMoney = MoneyTextWatcher.getValue(edMoney)
		} catch (e: Exception) {
			//debug("인트 변환 에러");
			inputMoney = 0
		}

		// getTaxFree
		val taxExemption: Long
		if (edOptionTaxFree.text.toString().length <= 1) {
			edOptionTaxFree.setText("0")
		}
		//taxExemption = Integer.parseInt(edOptionTaxFree.getText().toString());
		taxExemption = MoneyTextWatcher.getValue(edOptionTaxFree)

		// getFamily
		if (edOptionFamily.text.toString().length <= 1) {
			edOptionFamily.setText("1")
		}
		val family = Integer.parseInt(edOptionFamily.text.toString())

		// getChild
		val child: Int
		if (edOptionChild.text.toString().length <= 1) {
			edOptionChild.setText("0")
		}
		child = Integer.parseInt(edOptionChild.text.toString())

		val moneyType = findViewById(R.id.rgMoneyType) as RadioGroup

		if (R.id.raMoneyYearly == moneyType.checkedRadioButtonId) {
			annualBasis = true
		} else {
			annualBasis = false
		}

		/*
        Switch swSeverance = (Switch) findViewById(R.id.swYearlyOpSeverance);
        includedSeverance = swSeverance.isChecked();
        */

		val checkSeverance = findViewById(R.id.checkSeverance) as CheckBox
		includedSeverance = checkSeverance.isChecked
		//checkSeverance


		// 연산
		val calculator = Services.getInstance().calculator

		//옵션값 셋팅
		//CalculatorOptions options = ;
		calculator.options.setInputMoney(inputMoney.toDouble())
		calculator.options.taxExemption = taxExemption.toDouble()
		calculator.options.family = family
		calculator.options.child = child
		calculator.options.setAnnualBasis(annualBasis)
		calculator.options.setIncludedSeverance(includedSeverance)

		calculator.run()

		//결과 화면 호출
		val intent = Intent(activity, ReportActivity::class.java)
		startActivity(intent)
	}

	companion object {

		/**
		 */
		fun newInstance(): NormalCalculatorFragment {
			val fragment = NormalCalculatorFragment()
			val args = Bundle()
			fragment.arguments = args
			return fragment
		}
	}
}// Required empty public constructor
