package kr.asv.apps.salarycalculator.fragments.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_report_insurance.*
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.WordPageActivity
import kr.asv.apps.salarycalculator.fragments.BaseFragment
import kr.asv.apps.salarycalculator.R

class ReportInsuranceFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_insurance, container, false)
        setFragmentView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()

        infoNationalPension.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        infoEmployeeCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        infoHealthCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        infoLongTermCare.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
    }

    private fun onInformationDictionaryWord(view: View) {
        val id = view.id
        var keyId = ""
        when (id) {
            R.id.infoNationalPension -> {
                keyId = "national_pension"
            }
            R.id.infoHealthCare -> {
                keyId = "health_care"
            }
            R.id.infoLongTermCare -> {
                keyId = "long_term_care"
            }
            R.id.infoEmployeeCare -> {
                keyId = "employment_care"
            }
        }
        val intent = Intent(activity, WordPageActivity::class.java)
        intent.putExtra("wordId", keyId)
        startActivity(intent)
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

    companion object {

        fun newInstance(): ReportInsuranceFragment {
            val fragment = ReportInsuranceFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
