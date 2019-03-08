package kr.asv.apps.salarycalculator.fragments.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.fragments.BaseFragment
import kr.asv.apps.salarycalculator.R

/**
 */
class ReportSummaryFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report_summary, container, false)
        setFragmentView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()
    }

    private fun showResult() {
        //hideSoftKeyboard();
        val calculator = Services.calculator
        val format = java.text.DecimalFormat("###,##0")

        //실 수령액
        val txNetSalary = findViewById(R.id.txNetSalary) as TextView
        txNetSalary.text = format.format(calculator.netSalary)
        txNetSalary.append(" 원")

        //4대보험+세금합계
        val txMinusTotal = findViewById(R.id.txMinusTotal) as TextView
        val minusTotal = calculator.insurance.get() + calculator.incomeTax.get()
        txMinusTotal.text = format.format(minusTotal)
        txMinusTotal.append(" 원")

    }

    companion object {


        fun newInstance(): ReportSummaryFragment {
            val fragment = ReportSummaryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
