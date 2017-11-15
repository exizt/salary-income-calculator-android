package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import kr.asv.apps.salarycalculator.MoneyTextWatcher
import kr.asv.apps.salarycalculator.activities.ReportActivity
import kr.asv.apps.salarycalculator.Services
import kr.asv.calculators.salary.SalaryCalculator
import kr.asv.shhtaxmanager.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuickCalculatorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QuickCalculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuickCalculatorFragment : BaseFragment() {

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater!!.inflate(R.layout.fragment_quick_calculator, container, false)
		setFragmentView(view)
		setActionBarTitle(resources.getString(R.string.activity_title_quick_mode))
		val editMoney = findViewById(R.id.editMoney_QMode) as EditText
		editMoney.addTextChangedListener(MoneyTextWatcher(editMoney))
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initEventListener()
	}

	interface OnFragmentInteractionListener {
		fun onFragmentInteraction(uri: Uri)
	}

	private fun initEventListener() {
		// 계산하기 버튼 클릭시
		findViewById(R.id.btnExecute_QMode).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				onClickButtonCalculate()// 계산하기 버튼 클릭시
			}
		})

		// 금액 추가 버튼 +천만
		findViewById(R.id.btnPlus1000).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				addInputMoney(10000000)
			}
		})

		// 금액 추가 버튼 +백만
		findViewById(R.id.btnPlus100).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				addInputMoney(1000000)
			}
		})

		// 금액 추가 버튼 +십만
		findViewById(R.id.btnPlus10).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				addInputMoney(100000)
			}
		})

		// 금액 감소 버튼 -천만
		findViewById(R.id.btnMinus1000).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				minusInputMoney(10000000)
			}
		})

		// 금액 감소 버튼 -백만
		findViewById(R.id.btnMinus100).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				minusInputMoney(1000000)
			}
		})

		// 금액 감소 버튼 - 십만
		findViewById(R.id.btnMinus10).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				minusInputMoney(100000)
			}
		})

		// 금액 정정 버튼
		findViewById(R.id.btnClearInput_QM).setOnClickListener(object : Button.OnClickListener {
			override fun onClick(v: View) {
				initInputMoney()
			}
		})
	}

	private fun initInputMoney() {
		val editInputMoney = findViewById(R.id.editMoney_QMode) as EditText
		editInputMoney.setText("0")
	}

	private fun minusInputMoney(value: Int) {
		val editInputMoney = findViewById(R.id.editMoney_QMode) as EditText
		//long inputMoney = getValueEditText(R.id.editMoney_QMode);
		var inputMoney = MoneyTextWatcher.getValue(editInputMoney)
		/*
        if (editInputMoney.getText().length() <= 1) {
            inputMoney = 0;
        } else {
            inputMoney = Integer.parseInt(editInputMoney.getText().toString());
        }
        */
		inputMoney -= value.toLong()

		if (inputMoney < 0) inputMoney = 0
		editInputMoney.setText(inputMoney.toString())
	}

	private fun addInputMoney(value: Int) {
		val editInputMoney = findViewById(R.id.editMoney_QMode) as EditText
		//long inputMoney = getValueEditText(R.id.editMoney_QMode);
		var inputMoney = MoneyTextWatcher.getValue(editInputMoney)
		/*
        if (editInputMoney.getText().length() <= 1) {
            inputMoney = 0;
        } else {
            inputMoney = Integer.parseInt(editInputMoney.getText().toString());
        }
        */
		inputMoney += value.toLong()
		editInputMoney.setText(inputMoney.toString())
	}

	private fun onClickButtonCalculate() {
		val editInputMoney = findViewById(R.id.editMoney_QMode) as EditText
		// getMoney
		var inputMoney: Long
		if (editInputMoney.text.length <= 1) {
			return
		}
		try {
			//inputMoney = getValueEditText(R.id.editMoney_QMode);
			inputMoney = MoneyTextWatcher.getValue(editInputMoney)
		} catch (e: Exception) {
			//debug("인트 변환 에러");
			inputMoney = 0
		}

		/*
         * 연봉기준인지 월급기준인지 구분.
         * 1000만원 이상이면 연봉입력으로 생각하고 계산. (설마 월급이 천만원은 아니겠지)
         */
		var annualBasis = false
		if (inputMoney >= 10000000) {
			annualBasis = true
		}

		//옵션의 기본값
		// 비과세
		var taxExemption: Long = 100000
		// 부양가족수
		var family = 1
		// 20세 이하 자녀수
		var child = 0
		// 퇴직금 포함인지
		var includedSeverance = false


		//환경설정 값 가져오기.
		val pref = PreferenceManager.getDefaultSharedPreferences(activity)

		//[퀵계산 설정 사용] 일 때 세부옵션들을 불러온다.
		if (pref.getBoolean("quick_settings_enable", false)) {
			family = Integer.parseInt(pref.getString("quick_settings_family", "default"))
			child = Integer.parseInt(pref.getString("quick_settings_child", "0"))
			taxExemption = Integer.parseInt(pref.getString("quick_settings_tax_exemption", "100000")).toLong()
			includedSeverance = pref.getBoolean("quick_settings_severance", false)
		}

		val calculator = Services.getInstance().calculator

		//옵션값 셋팅
		calculator.options.setInputMoney(inputMoney.toDouble())
		calculator.options.taxExemption = taxExemption.toDouble()
		calculator.options.family = family
		calculator.options.child = child
		calculator.options.setAnnualBasis(annualBasis)
		calculator.options.setIncludedSeverance(includedSeverance)

		if (pref.getBoolean("rate_settings_enable", false)) {
			calculator.insurance.rates.nationalPension = java.lang.Double.parseDouble(pref.getString("rate_national_pension", "0"))
			calculator.insurance.rates.healthCare = java.lang.Double.parseDouble(pref.getString("rate_health_care", "0"))
			calculator.insurance.rates.longTermCare = java.lang.Double.parseDouble(pref.getString("rate_longterm_care", "0"))
			calculator.insurance.rates.employmentCare = java.lang.Double.parseDouble(pref.getString("rate_employment_care", "0"))
			//calculator.getInsurance().getRates().setEmploymentCare();
		} else {
			calculator.insurance.rates.init()
		}


		calculator.run()

		//결과 화면 호출
		val intent = Intent(activity, ReportActivity::class.java)
		startActivity(intent)
	}

	companion object {
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @return A new instance of fragment QuickCalculatorFragment.
		 */
		fun newInstance(): QuickCalculatorFragment {
			val fragment = QuickCalculatorFragment()
			val args = Bundle()
			fragment.arguments = args
			return fragment
		}
	}
}// Required empty public constructor
