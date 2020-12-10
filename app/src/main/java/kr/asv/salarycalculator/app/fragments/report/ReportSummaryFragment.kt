package kr.asv.salarycalculator.app.fragments.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.databinding.FragmentReportSummaryBinding
import kr.asv.salarycalculator.app.fragments.BaseFragment

/**
 */
class ReportSummaryFragment : BaseFragment() {
    private var _binding: FragmentReportSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.fragment_report_summary, container, false)
        _binding = FragmentReportSummaryBinding.inflate(inflater, container, false)
        val view = binding.root
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
        binding.netSalary.text = format.format(calculator.netSalary)
        binding.netSalary.append(" 원")

        //4대보험+세금합계
        val minusTotal = calculator.insurance.get() + calculator.incomeTax.get()
        binding.totalMinus.text = format.format(minusTotal)
        binding.totalMinus.append(" 원")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
