package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_older_calculator.*
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.androidutils.inputfilter.InputFilterMinMax
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.ReportActivity
import kr.asv.shhtaxmanager.R

/**
 * create an instance of this fragment.
 */
class NormalCalculatorFragment : BaseFragment() {
    private var annualBasis = false

    /**
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_older_calculator, container, false)
        setFragmentView(view)
        setActionBarTitle(resources.getString(R.string.nav_menu_detail_calculator))

        //세자리마다 쉼표표시
        val edMoney = findViewById(R.id.edMoney) as EditText
        val edOptionTaxFree = findViewById(R.id.edOptionTaxFree) as EditText
        edMoney.addTextChangedListener(MoneyTextWatcher(edMoney))
        edOptionTaxFree.addTextChangedListener(MoneyTextWatcher(edOptionTaxFree))

        // 최대 최소값 지정
        (findViewById(R.id.edMoney) as EditText).filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 999999999999))
        (findViewById(R.id.edOptionTaxFree) as EditText).filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 99999999999))
        (findViewById(R.id.edOptionFamily) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 20))
        (findViewById(R.id.edOptionChild) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 20))

        return view
    }

    /**
     *
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initEventListener()
        edMoney.requestFocus()

    }

    override fun onResume() {
        super.onResume()
        val pref = PreferenceManager.getDefaultSharedPreferences(this.activity)
        if (pref.getBoolean(resources.getString(R.string.pref_key_custom_rates_enabled), false)) {
            // 효과가 너무 약하다...강한 효과가 필요함...
            Toast.makeText(activity, "'세율설정' 을 사용중입니다. 설정을 취소하시려면 [환경설정 > 고급설정 (세율조정)] 을 변경해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     *
     */
    private fun initEventListener() {
        // 계산하기 버튼 클릭시
        btnCalculate.setOnClickListener {
            calculate()// 계산하기 버튼 클릭시
        }

        // 연봉 or 월급 선택시
        raMoneyYearly.setOnClickListener { v -> onClickMoneyType(v) }
        raMoneyMonthly.setOnClickListener { v -> onClickMoneyType(v) }

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
        //val txMoneyLabel = findViewById(R.id.titleMoneyLabel) as TextView
        //val layYearlyOpSeverance = findViewById(R.id.divYearlyOpSeverance) as LinearLayout
        if (radioButton.isChecked) {
            if (radioButton.id == R.id.raMoneyMonthly) {
                //this.annualBasis = false
                titleMoneyLabel.text = "월급입력"
                divYearlyOpSeverance.visibility = View.INVISIBLE
            } else {
                //this.annualBasis = true
                titleMoneyLabel.text = "연봉입력"
                divYearlyOpSeverance.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 세금 계산하기 버튼 클릭시 발생
     */
    private fun calculate() {

        // validate checking
        if (edMoney.text.length <= 1) {
            return
        }

        // get money value
        val inputMoney: Long = try {
            MoneyTextWatcher.getValue(edMoney)
        } catch (e: Exception) {
            0
        }

        // get taxfree value
        if (edOptionTaxFree.text.toString().length <= 1) {
            edOptionTaxFree.setText("0")
        }
        val taxExemption: Long = MoneyTextWatcher.getValue(edOptionTaxFree)

        // get family value
        if (edOptionFamily.text.toString().length <= 1) {
            edOptionFamily.setText("1")
        }
        val family = Integer.parseInt(edOptionFamily.text.toString())

        // get child value
        val child: Int = Integer.parseInt(edOptionChild.text.toString())
        if (edOptionChild.text.toString().length <= 1) {
            edOptionChild.setText("0")
        }

        if(inputMoney < taxExemption){
            return
        }

        // 연봉인지, 월급인지 구분값
        //annualBasis = R.id.raMoneyYearly == rgMoneyType.checkedRadioButtonId
        val annualBasis = raMoneyYearly.isChecked

        // 퇴직금 포함 여부
        val includedSeverance = checkSeverance.isChecked

        // 연산
        val calculator = Services.calculator

        //옵션값 셋팅
        val options = calculator.options
        options.inputMoney = inputMoney.toDouble()
        options.taxExemption = taxExemption.toDouble()
        options.family = family
        options.child = child
        options.setAnnualBasis(annualBasis)
        options.setIncludedSeverance(includedSeverance)
        options.setIncomeTaxCalculationDisabled(true)

        // 세율 정보 가져오기
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val rates = calculator.insurance.rates
        if (prefs.getBoolean(resources.getString(R.string.pref_key_custom_rates_enabled), false)) {
            rates.nationalPension = prefs.getString(resources.getString(R.string.pref_key_custom_national_pension_rate), "0").toDouble()
            rates.healthCare = prefs.getString(resources.getString(R.string.pref_key_custom_health_care_rate), "0").toDouble()
            rates.longTermCare = prefs.getString(resources.getString(R.string.pref_key_custom_long_term_care_rate), "0").toDouble()
            rates.employmentCare = prefs.getString(resources.getString(R.string.pref_key_custom_employment_care_rate), "0").toDouble()
        } else {
            Services.initInsuranceRates(prefs)
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

    companion object {
        /**
         */
        @Suppress("unused")
        fun newInstance(): NormalCalculatorFragment {
            val fragment = NormalCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
