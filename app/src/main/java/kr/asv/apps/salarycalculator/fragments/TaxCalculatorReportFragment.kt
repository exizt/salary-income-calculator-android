package kr.asv.apps.salarycalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services

/**
 */
class TaxCalculatorReportFragment : BaseFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_tax_calculator_report, container, false)
		setFragmentView(view)
		return view
	}


	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		hideSoftKeyboard()
		drawReport()
		initEventListener()
	}

	private fun initEventListener() {
		// 닫기 버튼 클릭시
		findViewById(R.id.id_btn_close).setOnClickListener{
				activity!!.supportFragmentManager.popBackStack()
		}
	}

	private fun drawReport() {
		val nationalRate = Services.instance.taxCalculatorRates.nationalRate
		val healthCareRate = Services.instance.taxCalculatorRates.healthCareRate
		val longTermCareRate = Services.instance.taxCalculatorRates.longTermCareRate
		val employmentCareRate = Services.instance.taxCalculatorRates.employmentCareRate

		val viewNationalRates = findViewById(R.id.id_view_national) as TextView
		val viewHealthCare = findViewById(R.id.id_view_health_care) as TextView
		val viewLongTermCare = findViewById(R.id.id_view_longterm_care) as TextView
		val viewEmploymentCare = findViewById(R.id.id_view_employment_care) as TextView

		viewNationalRates.text = nationalRate.toString()
		viewHealthCare.text = healthCareRate.toString()
		viewLongTermCare.text = longTermCareRate.toString()
		viewEmploymentCare.text = employmentCareRate.toString()
	}

	companion object {

		fun newInstance(): TaxCalculatorReportFragment {
			val fragment = TaxCalculatorReportFragment()
			val args = Bundle()
			fragment.arguments = args
			return fragment
		}
	}
}
