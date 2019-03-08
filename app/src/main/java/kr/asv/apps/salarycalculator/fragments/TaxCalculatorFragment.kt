package kr.asv.apps.salarycalculator.fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_tax_calculator.*
import kr.asv.androidutils.inputfilter.InputFilterLongMinMax
import kr.asv.androidutils.MoneyTextWatcher
import kr.asv.androidutils.inputfilter.InputFilterMinMax
import kr.asv.apps.salarycalculator.MainActivity
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.R

/**
 *
 */
class TaxCalculatorFragment : BaseFragment() {

    /**
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tax_calculator, container, false)
        setFragmentView(view)
        setActionBarTitle(resources.getString(R.string.nav_menu_rate_calculator))

        (findViewById(R.id.id_input_salary) as EditText).filters = arrayOf<InputFilter>(InputFilterLongMinMax(0, 999999999999))
        (findViewById(R.id.id_input_taxfree) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 10000000))
        (findViewById(R.id.id_input_national) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 10000000))
        (findViewById(R.id.id_input_health_care) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 10000000))
        (findViewById(R.id.id_input_longterm_care) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 10000000))
        (findViewById(R.id.id_input_employment_care) as EditText).filters = arrayOf<InputFilter>(InputFilterMinMax(0, 10000000))

        (findViewById(R.id.id_input_salary) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_salary) as EditText))
        (findViewById(R.id.id_input_taxfree) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_taxfree) as EditText))
        (findViewById(R.id.id_input_national) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_national) as EditText))
        (findViewById(R.id.id_input_health_care) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_health_care) as EditText))
        (findViewById(R.id.id_input_longterm_care) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_longterm_care) as EditText))
        (findViewById(R.id.id_input_employment_care) as EditText).addTextChangedListener(MoneyTextWatcher(findViewById(R.id.id_input_employment_care) as EditText))
        return view
    }

    /**
     *
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initEventListener()
    }

    /**
     *
     */
    private fun initEventListener() {
        // 계산하기 버튼 클릭시
        id_btn_calculate.setOnClickListener {
            onClickButtonCalculate()// 계산하기 버튼 클릭시
        }
    }

    /**
     * 세금 계산하기 버튼 클릭시 발생
     */
    private fun onClickButtonCalculate() {
        val salary = MoneyTextWatcher.getValue(findViewById(R.id.id_input_salary) as EditText)
        val taxfree = MoneyTextWatcher.getValue(findViewById(R.id.id_input_taxfree) as EditText)
        val national = MoneyTextWatcher.getValue(findViewById(R.id.id_input_national) as EditText)
        val healthCare = MoneyTextWatcher.getValue(findViewById(R.id.id_input_health_care) as EditText)
        val longTermCare = MoneyTextWatcher.getValue(findViewById(R.id.id_input_longterm_care) as EditText)
        val employmentCare = MoneyTextWatcher.getValue(findViewById(R.id.id_input_employment_care) as EditText)

        /*
        double salary = parseDouble((EditText) findViewById(R.id.id_input_salary));
        double taxfree = parseDouble((EditText) findViewById(R.id.id_input_taxfree));
        double national = parseDouble((EditText) findViewById(R.id.id_input_national));
        double healthCare = parseDouble((EditText) findViewById(R.id.id_input_healthcare));
        double longTermCare = parseDouble((EditText) findViewById(R.id.id_input_longtermcare));
        double employmentCare = parseDouble((EditText) findViewById(R.id.id_input_employmentcare));
        */


        val baseMoney = salary - taxfree

        Services.taxCalculatorRates.nationalRate = getRates(baseMoney, national)
        Services.taxCalculatorRates.healthCareRate = getRates(baseMoney, healthCare)
        Services.taxCalculatorRates.longTermCareRate = getRates(healthCare, longTermCare)
        Services.taxCalculatorRates.employmentCareRate = getRates(baseMoney, employmentCare)

        //결과 화면 호출
        //Intent intent=new Intent(getActivity(),ReportActivity.class);
        //startActivity(intent);
        val fragment = TaxCalculatorReportFragment.newInstance()
        assert(activity != null)
        (activity as MainActivity).replaceFragments(fragment)
    }

    /**
     * 이자율을 뽑아내는 계산식
     *
     * @param origin   int
     * @param interest int
     * @return double
     */
    @Suppress("unused")
    private fun getRates(origin: Int, interest: Int): Double =
            getRates(origin.toDouble(), interest.toDouble())

    /**
     *
     */
    private fun getRates(origin: Long, interest: Long): Double =
            getRates(origin.toDouble(), interest.toDouble())

    /**
     *
     */
    private fun getRates(origin: Double, interest: Double): Double {
        val result = interest / origin * 100.0
        return Math.round(result * 10000).toDouble() / 10000 //반올림
        //return (double)Math.floor(result * 10) / 10; //버림
        //return (double)Math.ceil(result * 10) / 10; //올림
        //return  ((double)interest / (double)origin) * 100.0;
    }

    /**
     *
     */
    @Suppress("unused")
    private fun parseInt(editText: EditText): Int = Integer.parseInt(editText.text.toString())

    /**
     *
     */
    @Suppress("unused")
    private fun parseDouble(editText: EditText): Double =
            java.lang.Double.parseDouble(editText.text.toString())

    companion object {

        /**
         */
        fun newInstance(): TaxCalculatorFragment {
            val fragment = TaxCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
