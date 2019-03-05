package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_older_calculator.*
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.ReportActivity

/**
 * create an instance of this fragment.
 */
class NormalCalculatorFragment : BaseFragment() {
    private var includedSeverance = false
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
        if (pref.getBoolean("rate_settings_enable", false)) {
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
                this.annualBasis = false
                titleMoneyLabel.text = "월급입력"
                divYearlyOpSeverance.visibility = View.INVISIBLE
            } else {
                this.annualBasis = true
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

        // 연봉인지, 월급인지 구분값
        //annualBasis = R.id.raMoneyYearly == rgMoneyType.checkedRadioButtonId
        annualBasis = raMoneyYearly.isChecked

        // 퇴직금 포함 여부
        includedSeverance = checkSeverance.isChecked

        // 연산
        val calculator = Services.calculator

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
        @Suppress("unused")
        fun newInstance(): NormalCalculatorFragment {
            val fragment = NormalCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
