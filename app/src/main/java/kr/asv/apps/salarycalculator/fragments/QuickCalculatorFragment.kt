package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_quick_calculator.*
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.androidutils.inputfilter.InputFilterMinMax
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.ReportActivity


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

        // 퀵 계산 모드인 경우에 알려준다.
        if (Services.isCustomRateMode()) {
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

        // 금액 정정 버튼
        btnClearInput_QM.setOnClickListener {
            idEditTextMoney.setText("0")
        }
        
        // 플러스, 마이너스 버튼 이벤트들
        pmButtonsEventListener()

        // 부양가족수 입력 필터
        idFamilyOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))

        // 20세이하 자녀수 입력 필터
        idChildOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99))

        // 비과세 입력 필터
        idTaxFreeOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 999999999))
    }

    /**
     * 증감 관련 버튼 이벤트 리스너
     */
    private fun pmButtonsEventListener(){
        // 금액 추가 버튼 +천만
        idBtnPlus1000.setOnClickListener {
            plusMinusInputMoney(10000000)
        }

        // 금액 추가 버튼 +백만
        idBtnPlus100.setOnClickListener {
            plusMinusInputMoney(1000000)
        }

        // 금액 추가 버튼 +십만
        idBtnPlus10.setOnClickListener {
            plusMinusInputMoney(100000)
        }

        // 금액 감소 버튼 -천만
        idBtnMinus1000.setOnClickListener {
            plusMinusInputMoney(-10000000)
        }

        // 금액 감소 버튼 -백만
        idBtnMinus100.setOnClickListener {
            plusMinusInputMoney(-1000000)
        }

        // 금액 감소 버튼 - 십만
        idBtnMinus10.setOnClickListener {
            plusMinusInputMoney(-100000)
        }
    }

    /**
     * 입력 금액에 증감을 하는 메서드.
     */
    private fun plusMinusInputMoney(value: Int) {
        val editText = findViewById(R.id.idEditTextMoney) as EditText
        //long inputMoney = getValueEditText(R.id.editMoney_QMode);
        var inputMoney = MoneyTextWatcher.getValue(editText)
        inputMoney += value.toLong()

        if (inputMoney < 0) inputMoney = 0
        editText.setText(inputMoney.toString())
    }

    /**
     * 숫자값을 가져올 때, 이상이 있거나 할 때에는 최소값으로 전환시킴.
     * 주의 Int 로 반환함.
     */
    private fun getEditTextNumber(editText: EditText, min: Int): Int{
        return try{
            val i= Integer.parseInt(editText.text.toString())
            if(i < min){
                idFamilyOption.setText(min.toString())
                min
            } else {
                i
            }
        } catch(e: Exception){
            editText.setText(min.toString())
            min
        }
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
        val inputMoney: Long = MoneyTextWatcher.getValue(idEditTextMoney)
        if(inputMoney <= 0){
            idEditTextMoney.setText("0")
        }

        // 부양 가족수
        // 1 보다 큰 값이어야 함. (물론 입력에서 체크하도록 해야함. 여기는 마지노선 체크)
        val family: Int = getEditTextNumber(idFamilyOption,1)

        // 20세 이하 자녀수
        val child: Int = getEditTextNumber(idChildOption,0)

        // 연봉기준인지, 월급기준인지 구분
        val annualBasis = raMoneyYearly.isChecked

        // 비과세 금액
        val taxFree: Long = MoneyTextWatcher.getValue(idTaxFreeOption)
        if(taxFree <= 0){
            idTaxFreeOption.setText("0")
        }

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
        options.inputMoney = inputMoney
        options.taxExemption = taxFree
        options.family = family
        options.child = child
        options.isAnnualBasis = annualBasis
        options.isIncludedSeverance = includedSeverance
        options.isIncomeTaxCalculationDisabled = true

        // 세율 정보 가져오기
        // 커스텀 세율 모드인 경우에는 커스텀 설정을 따름.
        if (Services.isCustomRateMode()) {
            val cKeys = Services.AppPrefKeys.CustomRates
            val rates = calculator.insurance.rates
            rates.nationalPension = Services.getAppPrefValue(cKeys.nationalPension) as Double
            rates.healthCare = Services.getAppPrefValue(cKeys.healthCare) as Double
            rates.longTermCare = Services.getAppPrefValue(cKeys.longTermCare) as Double
            rates.employmentCare = Services.getAppPrefValue(cKeys.employmentCare) as Double
        } else {
            Services.setInsuranceRatesToDefault()
        }

        //
        calculator.prepare()

        // 소득세 계산 (데이터베이스 에서 읽어오기)
        val incomeTaxDao = Services.getIncomeTaxDao()
        incomeTaxDao.isDebug = isDebug
        val earnedIncomeTax = incomeTaxDao.getValue(calculator.salary.basicSalary, family, "201802")
        debug("incomeTax:",earnedIncomeTax)

        // 실수령액 계산
        calculator.run(earnedIncomeTax)

        //결과 화면 호출
        val intent = Intent(activity, ReportActivity::class.java)
        startActivity(intent)
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER", "SameParameterValue")
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
