package kr.asv.apps.salarycalculator.fragments.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.text.DecimalFormat

import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.fragments.BaseFragment
import kr.asv.calculators.salary.SalaryCalculator
import kr.asv.apps.salarycalculator.R

class ReportTaxFragment : BaseFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_report_tax, container, false)
		setFragmentView(view)
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		showResult()
	}

	private fun showResult() {
		val calculator = Services.getInstance().calculator

		val format = java.text.DecimalFormat("###,##0")

		//소득세
		val txEarnedTax = findViewById(R.id.txEarnedTax) as TextView
		txEarnedTax.text = format.format(calculator.incomeTax.earnedIncomeTax)
		txEarnedTax.append(" 원")

		//지방세(주민세)
		val txLocalTax = findViewById(R.id.txLocalTax) as TextView
		txLocalTax.text = format.format(calculator.incomeTax.localTax)
		txLocalTax.append(" 원")
	}

	companion object {

		fun newInstance(): ReportTaxFragment {
			val fragment = ReportTaxFragment()
			val args = Bundle()
			fragment.arguments = args
			return fragment
		}
	}
}
