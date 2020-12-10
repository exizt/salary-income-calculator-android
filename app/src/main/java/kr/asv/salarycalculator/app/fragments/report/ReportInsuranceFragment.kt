package kr.asv.salarycalculator.app.fragments.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.fragment_report_insurance, container, false)
        _binding = FragmentReportInsuranceBinding.inflate(inflater, container, false)
        val view = binding.root
        setFragmentView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()

        binding.infoNationalPension.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoEmployeeCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoHealthCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoLongTermCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
    }

    private fun onInformationDictionaryWord(view: View) {
        var termCid = ""
        when (view.id) {
            R.id.infoNationalPension -> {
                termCid = "national_pension"
            }
            R.id.infoHealthCare -> {
                termCid = "health_care"
            }
            R.id.infoLongTermCare -> {
                termCid = "long_term_care"
            }
            R.id.infoEmployeeCare -> {
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
        val txNationPension = findViewById(R.id.txNationPension) as TextView
        txNationPension.text = format.format(calculator.insurance.nationalPension)
        txNationPension.append(" 원")

        //건강보험료
        val txHealthCare = findViewById(R.id.txHealthCare) as TextView
        txHealthCare.text = format.format(calculator.insurance.healthCare)
        txHealthCare.append(" 원")

        //요양보험료
        val txLongtermCare = findViewById(R.id.txLongtermCare) as TextView
        txLongtermCare.text = format.format(calculator.insurance.longTermCare)
        txLongtermCare.append(" 원")

        //고용보험
        val txEmploymentCare = findViewById(R.id.txEmploymentCare) as TextView
        txEmploymentCare.text = format.format(calculator.insurance.employmentCare)
        txEmploymentCare.append(" 원")

    }

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
