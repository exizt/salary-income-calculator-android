package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_quick_calculator.*
import kotlinx.android.synthetic.main.fragment_quick_calculator.checkSeverance
import kotlinx.android.synthetic.main.fragment_quick_calculator.edOptionChild
import kotlinx.android.synthetic.main.fragment_quick_calculator.edOptionFamily
import kotlinx.android.synthetic.main.fragment_quick_calculator.edOptionTaxFree
import kotlinx.android.synthetic.main.fragment_quick_calculator.raMoneyYearly
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.ReportActivity
import kr.asv.apps.salarycalculator.R


/**
 * 심플 연봉 계산기
 * 특정 수치를 기준으로, 연봉 or 월급을 나누고 간이 계산한다.
 */
class QuickCalculatorFragment : BaseFragment() {
    private val isDebug = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quick_calculator, container, false)
        setFragmentView(view)
        setActionBarTitle(resources.getString(R.string.nav_menu_quick_calculator))

        val editMoney = findViewById(R.id.idEditTextMoney) as EditText
        editMoney.addTextChangedListener(MoneyTextWatcher(editMoney))
        editMoney.filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 999999999999))

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initEventListeners()

        idEditTextMoney.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        //[퀵계산 설정 사용] 일 때 세부옵션들을 불러온다.
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.activity)
        if (prefs.getBoolean(getString(R.string.pref_key_custom_rates_enabled), false)) {
            Toast.makeText(activity, "'세율설정' 을 사용중입니다. 설정을 취소하시려면 [환경설정 > 고급설정 (세율조정)] 을 변경해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("unused")
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    /**
     * click event listener 들을 모아둔 메서드.
     */
    private fun initEventListeners() {
        // 계산하기 버튼 클릭시
        btnExecute_QMode.setOnClickListener {
            calculate()// 계산하기 버튼 클릭시
        }

        // 금액 추가 버튼 +천만
        btnPlus1000.setOnClickListener {
            addInputMoney(10000000)
        }

        // 금액 추가 버튼 +백만
        btnPlus100.setOnClickListener {
            addInputMoney(1000000)
        }

        // 금액 추가 버튼 +십만
        btnPlus10.setOnClickListener {
            addInputMoney(100000)
        }

        // 금액 감소 버튼 -천만
        btnMinus1000.setOnClickListener {
            minusInputMoney(10000000)
        }

        // 금액 감소 버튼 -백만
        btnMinus100.setOnClickListener {
            minusInputMoney(1000000)
        }

        // 금액 감소 버튼 - 십만
        btnMinus10.setOnClickListener {
            minusInputMoney(100000)
        }

        // 금액 정정 버튼
        btnClearInput_QM.setOnClickListener {
            idEditTextMoney.setText("0")
        }
    }

    /**
     *
     */
    private fun minusInputMoney(value: Int) {
        val editInputMoney = findViewById(R.id.idEditTextMoney) as EditText
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

    /**
     *
     */
    private fun addInputMoney(value: Int) {
        val editInputMoney = findViewById(R.id.idEditTextMoney) as EditText
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

    /**
     * 계산하는 동작
     */
    private fun calculate() {
        // validate checking
        if (idEditTextMoney.text.length <= 1) {
            return
        }

        // 입력 금액
        val inputMoney: Long = try {
            MoneyTextWatcher.getValue(idEditTextMoney)
        } catch (e: Exception) {
            0
        }

        // 부양 가족수
        if (edOptionFamily.text.toString().length <= 1) {
            edOptionFamily.setText("1")
        }
        val family = Integer.parseInt(edOptionFamily.text.toString())

        // 20세 이하 자녀수
        val child: Int = Integer.parseInt(edOptionChild.text.toString())
        if (edOptionChild.text.toString().length <= 1) {
            edOptionChild.setText("0")
        }

        // 연봉기준인지, 월급기준인지 구분
        val annualBasis = raMoneyYearly.isChecked

        // 비과세 금액
        if (edOptionTaxFree.text.toString().length <= 1) {
            edOptionTaxFree.setText("0")
        }
        val taxFree: Long = MoneyTextWatcher.getValue(edOptionTaxFree)

        //옵션의 기본값
        val includedSeverance = checkSeverance.isChecked // 퇴직금 포함인지

        // 비과세보다 적은 경우는 그냥 계산하지 말기.
        if(inputMoney < taxFree){
            return
        }
        
        // 실수령액 계산기 클래스
        val calculator = Services.calculator

        // 옵션값 셋팅
        val options = calculator.options
        options.inputMoney = inputMoney.toDouble()
        options.taxExemption = taxFree.toDouble()
        options.family = family
        options.child = child
        options.setAnnualBasis(annualBasis)
        options.setIncludedSeverance(includedSeverance)
        options.setIncomeTaxCalculationDisabled(true)

        // 세율 정보 가져오기
        // 커스텀 설정했을 경우에는 preferences 의 값을 가져오고, 아닌 경우에는 기본값들을 가져온다.
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (prefs.getBoolean(resources.getString(R.string.pref_key_custom_rates_enabled), false)) {
            val rates = calculator.insurance.rates
            rates.nationalPension = prefs.getString(resources.getString(R.string.pref_key_custom_national_pension_rate), "0")?.toDouble() ?:0.0
            rates.healthCare = prefs.getString(resources.getString(R.string.pref_key_custom_health_care_rate), "0")?.toDouble() ?:0.0
            rates.longTermCare = prefs.getString(resources.getString(R.string.pref_key_custom_long_term_care_rate), "0")?.toDouble() ?:0.0
            rates.employmentCare = prefs.getString(resources.getString(R.string.pref_key_custom_employment_care_rate), "0")?.toDouble() ?:0.0
        } else {
            Services.setInsuranceRatesToDefault()
        }

        // 연봉, 월급, 4대 보험 계산
        calculator.calculateSalariesWithInsurances()

        // 소득세 계산 (데이터베이스 에서 읽어오기)
        val incomeTaxDao = Services.getIncomeTaxDao()
        calculator.incomeTax.earnedIncomeTax = incomeTaxDao.getValue(calculator.salary.basicSalary.toLong(), family, "201802").toDouble()

        // 실수령액 계산
        calculator.calculateOnlyNetSalary()

        //결과 화면 호출
        val intent = Intent(activity, ReportActivity::class.java)
        startActivity(intent)
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        if (isDebug) {
            Services.debugLog("QuickCalculatorFragment", msg, msg2)
        }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment QuickCalculatorFragment.
         */
        @Suppress("unused")
        fun newInstance(): QuickCalculatorFragment {
            val fragment = QuickCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
