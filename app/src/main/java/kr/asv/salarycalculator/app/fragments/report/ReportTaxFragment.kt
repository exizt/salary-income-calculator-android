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
import kr.asv.salarycalculator.app.databinding.FragmentReportTaxBinding
import kr.asv.salarycalculator.app.fragments.BaseFragment

class ReportTaxFragment : BaseFragment() {
    private var _binding: FragmentReportTaxBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.fragment_report_tax, container, false)
        _binding = FragmentReportTaxBinding.inflate(inflater, container, false)
        val view = binding.root
        setFragmentView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showResult()

        binding.infoIncomeTax.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        binding.infoLocalTax.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
    }

    private fun onInformationDictionaryWord(view: View) {
        var termCid = ""
        when (view.id) {
            R.id.infoIncomeTax -> {
                termCid = "income_tax"
            }
            R.id.infoLocalTax -> {
                termCid = "income_local_tax"
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

        //소득세
        val txEarnedTax = findViewById(R.id.txEarnedTax) as TextView
        txEarnedTax.text = format.format(calculator.incomeTax.earnedIncomeTax)
        txEarnedTax.append(" 원")

        //지방세(주민세)
        val txLocalTax = findViewById(R.id.txLocalTax) as TextView
        txLocalTax.text = format.format(calculator.incomeTax.localTax)
        txLocalTax.append(" 원")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
