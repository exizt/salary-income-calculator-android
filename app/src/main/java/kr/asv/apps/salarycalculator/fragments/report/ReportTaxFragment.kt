package kr.asv.apps.salarycalculator.fragments.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_report_tax.*
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.WordActivity
import kr.asv.apps.salarycalculator.fragments.BaseFragment
import kr.asv.shhtaxmanager.R

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

        infoIncomeTax.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
        infoLocalTax.setOnClickListener { v -> onInformationDictionaryWord(v!!) }
    }

    private fun onInformationDictionaryWord(view: View) {
        val id = view.id
        var keyId = ""
        when (id) {
            R.id.infoIncomeTax -> {
                keyId = "income_tax"
            }
            R.id.infoLocalTax -> {
                keyId = "income_local_tax"
            }
        }
        val intent = Intent(activity, WordActivity::class.java)
        intent.putExtra("wordId", keyId)
        startActivity(intent)
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

    companion object {

        fun newInstance(): ReportTaxFragment {
            val fragment = ReportTaxFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
