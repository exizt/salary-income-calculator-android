package kr.asv.salarycalculator.app.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.activities.ReportActivity
import kr.asv.salarycalculator.app.databinding.FragmentQuickCalculatorBinding
import kr.asv.salarycalculator.utils.MoneyTextWatcher
import kr.asv.salarycalculator.utils.inputfilter.InputFilterLongMinMax
import kr.asv.salarycalculator.utils.inputfilter.InputFilterMinMax


/**
 * 심플 연봉 계산기
 * 특정 수치를 기준으로, 연봉 or 월급을 나누고 간이 계산한다.
 */
class QuickCalculatorFragment : BaseFragment() {
    // debug option
    private val isDebug = true
    // view binding
    private var _binding: FragmentQuickCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        debug("onCreateView")
        //val view = inflater.inflate(R.layout.fragment_quick_calculator, container, false)
        _binding = FragmentQuickCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root
        //setFragmentView(view)
        setActionBarTitle(resources.getString(R.string.nav_menu_quick_calculator))
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        debug("onActivityCreated")
        // (findViewById(R.id.input_money) as EditText).apply{
        binding.inputMoney.apply{
            //addTextChangedListener(MoneyTextWatcher(this))
            filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 999999999999))
            addTextChangedListener(inputMoneyTextWatcher())
        }
        //debug("onActivityCreated")
        initEventListeners()

        // 기본값으로 보여줌.
        initDisplayDefaultOptions()
        //idEditTextMoney.requestFocus()
    }

    override fun onResume() {
        super.onResume()

        // 퀵 계산 모드인 경우에 알려준다.
        if (Services.isCustomRateMode()) {
            Toast.makeText(activity, "'세율설정' 을 사용중입니다. 설정을 취소하시려면 [환경설정 > 고급설정 (세율조정)] 을 변경해주세요.", Toast.LENGTH_LONG).show()
        }
        // this.activity?.let { Services.loadDatabase(it) }
    }

    private fun inputMoneyTextWatcher(): TextWatcher{
        return object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //입력하기 전에
                //debug("[inputMoneyTextWatcher] beforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //입력되는 텍스트에 변화가 있을 때
                //debug("[inputMoneyTextWatcher] onTextChanged")
            }

            override fun afterTextChanged(s: Editable) {
                //입력이 끝났을 때
                //debug("[inputMoneyTextWatcher] afterTextChanged")
                //debug(s.toString())
                val l = s.toString().toLongOrNull()
                changeYearlyOptions(l)
            }
        }
    }

    /**
     * 기준 금액에 따라 연간/월간 옵션을 변경해주는 메서드.
     */
    fun changeYearlyOptions(money: Long?){
        money?.let{
            if(it >= 10000000){
                //
                binding.yearlyOption.isChecked = true
                binding.monthlyOption.isChecked = false
            } else {
                //
                binding.yearlyOption.isChecked = false
                binding.monthlyOption.isChecked = true
            }
        }
    }

    /**
     * 기본 입력값 적용
     */
    private fun initDisplayDefaultOptions(){
        // 기본 입력값
        val money = Services.AppPref.getString(Services.AppPref.Keys.DefaultInput.money,"0")
        binding.inputMoney.setText(money)

        //val taxFree = Services.getAppPrefValue(Services.AppPrefKeys.DefaultInput.taxFree) as? String
        val taxFree = Services.AppPref.getString(Services.AppPref.Keys.DefaultInput.taxFree,"0")
        binding.taxFreeOption.setText(taxFree)

        val family = Services.AppPref.getString(Services.AppPref.Keys.DefaultInput.family,"1")
        binding.familyOption.setText(family)

        val child = Services.AppPref.getString(Services.AppPref.Keys.DefaultInput.child, "0")
        binding.childOption.setText(child)
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
        binding.btnCalculate.setOnClickListener {
            calculate()// 계산하기 버튼 클릭시
        }

        // 금액 정정 버튼
        binding.btnInputClear.setOnClickListener {
            binding.inputMoney.setText("0")
        }
        
        // 플러스, 마이너스 버튼 이벤트들
        pmButtonsEventListener()

        // 부양가족수 입력 필터
        binding.familyOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(1, 99))

        // 20세이하 자녀수 입력 필터
        binding.childOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 99))

        // 비과세 입력 필터
        binding.taxFreeOption.text.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 999999999))

    }

    /**
     * 증감 관련 버튼 이벤트 리스너
     */
    private fun pmButtonsEventListener(){
        // 금액 추가 버튼 +천만
        binding.btnPlus1000.setOnClickListener {
            plusMinusInputMoney(10000000)
        }

        // 금액 추가 버튼 +백만
        binding.btnPlus100.setOnClickListener {
            plusMinusInputMoney(1000000)
        }

        // 금액 추가 버튼 +십만
        binding.btnPlus10.setOnClickListener {
            plusMinusInputMoney(100000)
        }
        /*
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
        */
    }

    /**
     * 입력 금액에 증감을 하는 메서드.
     */
    private fun plusMinusInputMoney(value: Int) {
        //val editText = findViewById(R.id.input_money) as EditText
        //long inputMoney = getValueEditText(R.id.editMoney_QMode);
        var money = MoneyTextWatcher.getValue(binding.inputMoney)
        money += value.toLong()

        if (money < 0) money = 0
        binding.inputMoney.setText(money.toString())
    }

    /**
     * 숫자값을 가져올 때, 이상이 있거나 할 때에는 최소값으로 전환시킴.
     * 주의 Int 로 반환함.
     */
    private fun getEditTextNumber(editText: EditText, min: Int): Int{
        return try{
            val i= Integer.parseInt(editText.text.toString())
            if(i < min){
                binding.familyOption.setText(min.toString())
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
        if (binding.inputMoney.text.length <= 3) {
            return
        }

        // 입력 금액
        val inputMoney: Long = MoneyTextWatcher.getValue(binding.inputMoney)
        if(inputMoney <= 0){
            binding.inputMoney.setText("0")
        }

        // 부양 가족수
        // 1 보다 큰 값이어야 함. (물론 입력에서 체크하도록 해야함. 여기는 마지노선 체크)
        val family: Int = getEditTextNumber(binding.familyOption,1)

        // 20세 이하 자녀수
        val child: Int = getEditTextNumber(binding.childOption,0)

        // 비과세 금액
        val taxFree: Long = MoneyTextWatcher.getValue(binding.taxFreeOption)
        if(taxFree <= 0){
            binding.taxFreeOption.setText("0")
        }

        //옵션의 기본값
        //val includedSeverance = checkSeverance.isChecked // 퇴직금 포함인지

        // 비과세보다 적은 경우는 그냥 계산하지 말기.
        if(inputMoney < taxFree){
            return
        }
        
        // 실수령액 계산기 클래스 및 옵션값 셋팅
        val calculator = Services.calculator
        val options = calculator.options
        options.inputMoney = inputMoney
        options.taxExemption = taxFree
        options.family = family
        options.child = child
        // 연봉기준인지, 월급기준인지 구분
        options.isAnnualBasis = binding.yearlyOption.isChecked
        //options.isIncludedSeverance = includedSeverance
        options.isIncomeTaxCalculationDisabled = true

        // 세율 정보 가져오기
        // 커스텀 세율 모드인 경우에는 커스텀 설정을 따름.
        if (Services.isCustomRateMode()) {
            debug("[calculate] 커스텀 세율로 계산")
            val cKeys = Services.AppPref.Keys.CustomRates
            val rates = calculator.insurance.rates
            rates.nationalPension = Services.AppPref.getString(cKeys.nationalPension, "0").toDouble()
            rates.healthCare = Services.AppPref.getString(cKeys.healthCare, "0").toDouble()
            rates.longTermCare = Services.AppPref.getString(cKeys.longTermCare, "0").toDouble()
            rates.employmentCare = Services.AppPref.getString(cKeys.employmentCare, "0").toDouble()

            //rates.nationalPension = Services.getAppPrefValue(cKeys.nationalPension) as Double
            //rates.healthCare = Services.getAppPrefValue(cKeys.healthCare) as Double
            //rates.longTermCare = Services.getAppPrefValue(cKeys.longTermCare) as Double
            //rates.employmentCare = Services.getAppPrefValue(cKeys.employmentCare) as Double
        } else {
            debug("[calculate] 기본값 세율로 계산")
            Services.setInsuranceRatesToDefault()
        }

        // 비과세 제외 계산 등
        calculator.calculateSalary()

        // 소득세 계산 (데이터베이스 에서 읽어오기)
        //val incomeTaxDao = Services.getIncomeTaxDao()
        //incomeTaxDao.isDebug = isDebug
        //val earnedIncomeTax = incomeTaxDao.getValue(calculator.salary.basicSalary, family, "201802")
        //debug("incomeTax:",earnedIncomeTax)

        // 실수령액 계산
        calculator.run()

        //결과 화면 호출
        val intent = Intent(activity, ReportActivity::class.java)
        startActivity(intent)
    }

    /**
     * view 소멸 이벤트
     * view binding 메모리 해제 구문 추가.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
