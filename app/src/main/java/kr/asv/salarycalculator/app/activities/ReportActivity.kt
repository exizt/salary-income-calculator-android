package kr.asv.salarycalculator.app.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_report.*
import kr.asv.salarycalculator.utils.AdmobAdapter
import kr.asv.salarycalculator.app.fragments.report.ReportInputFragment
import kr.asv.salarycalculator.app.fragments.report.ReportInsuranceFragment
import kr.asv.salarycalculator.app.fragments.report.ReportSummaryFragment
import kr.asv.salarycalculator.app.fragments.report.ReportTaxFragment
import kr.asv.salarycalculator.app.R

class ReportActivity : AppCompatActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        title = "실수령액 조회 결과"

        //로드하면서 결과값을 조회해온다.
        setResultReport()

        // Admob 호출
        AdmobAdapter.loadBannerAdMob(adView)
    }

    /**
     * 네비게이션 백버튼 클릭시, 메인액티비티를 recreate 를 하는 것을 방지하려는 목적.
     * @return
     */
    override fun getSupportParentActivityIntent(): Intent? {
        finish()
        return null
    }

    private fun setResultReport() {
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return ReportSummaryFragment.newInstance()
                1 -> return ReportInsuranceFragment.newInstance()
                2 -> return ReportTaxFragment.newInstance()
                3 -> return ReportInputFragment.newInstance()
            }
            return ReportSummaryFragment.newInstance()
        }

        /**
         * 전체 갯수인가? 보여질 갯수인가? 나중에 확인해봐야함
         * @return
         */
        override fun getCount(): Int = 4
    }
}