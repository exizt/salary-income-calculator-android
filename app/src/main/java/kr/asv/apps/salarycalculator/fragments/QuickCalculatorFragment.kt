package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_quick_calculator.*
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.ReportActivity
import kr.asv.shhtaxmanager.R

/**
 *
 *
 */
class QuickCalculatorFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quick_calculator, container, false)
        setFragmentView(view)
        setActionBarTitle(resources.getString(R.string.nav_menu_quick_calculator))

        val editMoney = findViewById(R.id.editMoney_QMode) as EditText
        editMoney.addTextChangedListener(MoneyTextWatcher(editMoney))
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initEventListeners()

        editMoney_QMode.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        //[퀵계산 설정 사용] 일 때 세부옵션들을 불러온다.
        val pref = PreferenceManager.getDefaultSharedPreferences(this.activity)
        if (pref.getBoolean("quick_settings_enable", false)) {
            Toast.makeText(activity, "'퀵계산설정' 을 사용중입니다. 설정을 취소하시려면 [환경설정 > 퀵계산 설정] 을 변경해주세요.", Toast.LENGTH_LONG).show()
        }
        if (pref.getBoolean("rate_settings_enable", false)) {
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
            editMoney_QMode.setText("0")
        }
    }

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     * 계산하는 동작
     * @todo 세율의 기본값을 가져오는 루틴을 손 봐야 한다.
     */
    private fun calculate() {

        // validate checking
        if (editMoney_QMode.text.length <= 1) {
            return
        }
        //val editInputMoney = findViewById(R.id.editMoney_QMode) as EditText

        // get money value
        val inputMoney: Long = try {
            MoneyTextWatcher.getValue(editMoney_QMode)
        } catch (e: Exception) {
            0
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
        var taxExemption: Long = 100000 // 비과세
        var family = 1 // 부양가족수
        var child = 0 // 20세 이하 자녀수
        var includedSeverance = false // 퇴직금 포함인지


        //환경설정 값 가져오기.
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        //[퀵계산 설정 사용] 일 때 세부옵션들을 불러온다.
        if (prefs.getBoolean("quick_settings_enable", false)) {
            family = Integer.parseInt(prefs.getString("quick_settings_family", "default"))
            child = Integer.parseInt(prefs.getString("quick_settings_child", "0"))
            taxExemption = Integer.parseInt(prefs.getString("quick_settings_tax_exemption", "100000")).toLong()
            includedSeverance = prefs.getBoolean("quick_settings_severance", false)
        }

        val calculator = Services.calculator
        val options = calculator.options

        //옵션값 셋팅
        options.setInputMoney(inputMoney.toDouble())
        options.taxExemption = taxExemption.toDouble()
        options.family = family
        options.child = child
        options.setAnnualBasis(annualBasis)
        options.setIncludedSeverance(includedSeverance)


        val rates = calculator.insurance.rates
        if (prefs.getBoolean("rate_settings_enable", false)) {
            rates.nationalPension = prefs.getString(resources.getString(R.string.pref_key_custom_national_pension_rate), "0").toDouble()
            rates.healthCare = prefs.getString(resources.getString(R.string.pref_key_custom_health_care_rate), "0").toDouble()
            rates.longTermCare = prefs.getString(resources.getString(R.string.pref_key_custom_long_term_care_rate), "0").toDouble()
            rates.employmentCare = prefs.getString(resources.getString(R.string.pref_key_custom_employment_care_rate), "0").toDouble()
        } else {
            Services.setDefaultInsuranceRatesInitialize(this.activity!!)
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
        @Suppress("unused")
        fun newInstance(): QuickCalculatorFragment {
            val fragment = QuickCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
