package kr.asv.salarycalculator.app.fragments.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.activities.WordPageActivity
import kr.asv.salarycalculator.app.databinding.FragmentReportInsuranceBinding
import kr.asv.salarycalculator.app.fragments.BaseFragment

class ReportInsuranceFragment : BaseFragment() {
    // view binding
    private var _binding: FragmentReportInsuranceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentReportInsuranceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()

        binding.infoNationalPension.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoEmploymentCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoHealthCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoLongTermCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
    }

    private fun onInformationDictionaryWord(view: View) {
        var termCid = ""
        when (view.id) {
            R.id.info_national_pension -> {
                termCid = "national_pension"
            }
            R.id.info_health_care -> {
                termCid = "health_care"
            }
            R.id.info_long_term_care -> {
                termCid = "long_term_care"
            }
            R.id.info_employment_care -> {
                termCid = "employment_care"
            }
        }
        if(termCid != ""){
            val intent = Intent(activity, WordPageActivity::class.java)
            intent.putExtra("termCid", termCid)
            startActivity(intent)
        }
    }

    private fun showResult() {
        val calculator = Services.calculator
        val format = java.text.DecimalFormat("###,##0")

        //국민연금
        binding.nationalPension.text = format.format(calculator.insurance.nationalPension)
        binding.nationalPension.append(" 원")

        //건강보험료
        binding.healthCare.text = format.format(calculator.insurance.healthCare)
        binding.healthCare.append(" 원")

        //요양보험료
        binding.longTermCare.text = format.format(calculator.insurance.longTermCare)
        binding.longTermCare.append(" 원")

        //고용보험
        binding.employmentCare.text = format.format(calculator.insurance.employmentCare)
        binding.employmentCare.append(" 원")

    }

    /**
     * view 소멸 이벤트
     * view binding 메모리 해제 구문 추가.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance(): ReportInsuranceFragment {
            val fragment = ReportInsuranceFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
