package kr.asv.salarycalculator.app.fragments.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                              savedInstanceState: Bundle?): View {
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
        binding.annualSalary.text = format.format(calculator.salary.grossAnnualSalary)
        binding.annualSalary.append(" 원")

        //계산된 월급
        binding.monthlySalary.text = format.format(calculator.salary.grossSalary)
        binding.monthlySalary.append(" 원")

        //부양가족수
        binding.familyOption.text = String.format("%d 명", calculator.options.family)

        //20세이하자녀수
        binding.childOption.text = String.format("%d 명", calculator.options.child)

        //입력한 비과세액
        binding.taxFreeOption.text = format.format(calculator.options.taxExemption)
        binding.taxFreeOption.append(" 원")

        // 세율 값도 보여주게 처리.
        val rateFormat = java.text.DecimalFormat("###,##0.####%")
        binding.nationalPensionRate.text = rateFormat.format(calculator.insurance.rates.nationalPension)
        binding.healthCareRate.text = rateFormat.format(calculator.insurance.rates.healthCare)
        binding.longTermCareRate.text = rateFormat.format(calculator.insurance.rates.longTermCare)
        binding.employmentCareRate.text = rateFormat.format(calculator.insurance.rates.employmentCare)

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
