package kr.asv.salarycalculator.app.fragments.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.databinding.FragmentReportInputBinding
import kr.asv.salarycalculator.app.fragments.BaseFragment

/**
 */
class ReportInputFragment : BaseFragment() {
    // view binding
    private var _binding: FragmentReportInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.fragment_report_input, container, false)
        _binding = FragmentReportInputBinding.inflate(inflater, container, false)
        val view = binding.root
        setFragmentView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()
    }

    @SuppressLint("DefaultLocale")
    private fun showResult() {
        val calculator = Services.calculator

        val format = java.text.DecimalFormat("###,##0")

        //계산된 연봉
        val txInputSalaryAnnual = findViewById(R.id.txInputSalaryAnnual) as TextView
        txInputSalaryAnnual.text = format.format(calculator.salary.grossAnnualSalary)
        txInputSalaryAnnual.append(" 원")

        //계산된 월급
        val txInputSalary = findViewById(R.id.txInputSalary) as TextView
        txInputSalary.text = format.format(calculator.salary.grossSalary)
        txInputSalary.append(" 원")

        //부양가족수
        val txInputFamily = findViewById(R.id.txInputFamily) as TextView
        txInputFamily.text = String.format("%d 명", calculator.options.family)

        //20세이하자녀수
        val txInputChild = findViewById(R.id.txInputChild) as TextView
        txInputChild.text = String.format("%d 명", calculator.options.child)

        //입력한 비과세액
        val txInputTaxExemption = findViewById(R.id.txInputTaxExemption) as TextView
        txInputTaxExemption.text = format.format(calculator.options.taxExemption)
        txInputTaxExemption.append(" 원")

        // 세율 값도 보여주게 처리.
        val rateFormat = java.text.DecimalFormat("###,##0.####%")
        (findViewById(R.id.txNationPensionRate) as TextView).text = rateFormat.format(calculator.insurance.rates.nationalPension)
        (findViewById(R.id.txHealthCareRate) as TextView).text = rateFormat.format(calculator.insurance.rates.healthCare)
        (findViewById(R.id.txLongtermCareRate) as TextView).text = rateFormat.format(calculator.insurance.rates.longTermCare)
        (findViewById(R.id.txEmploymentCareRate) as TextView).text = rateFormat.format(calculator.insurance.rates.employmentCare)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(): ReportInputFragment {
            val fragment = ReportInputFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
