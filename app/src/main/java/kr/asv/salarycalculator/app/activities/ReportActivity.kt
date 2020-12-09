package kr.asv.salarycalculator.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report.ad_container
import kr.asv.salarycalculator.utils.AdmobAdapter
import kr.asv.salarycalculator.app.fragments.report.ReportInputFragment
import kr.asv.salarycalculator.app.fragments.report.ReportInsuranceFragment
import kr.asv.salarycalculator.app.fragments.report.ReportSummaryFragment
import kr.asv.salarycalculator.app.fragments.report.ReportTaxFragment
import kr.asv.salarycalculator.app.R

class ReportActivity : AppCompatActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    // AdView 관련
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private val adaptiveAdSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = ad_container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    /**
     * onCreate
     */
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
        // AdmobAdapter.loadBannerAdMob(adView)
        // Admob 호출
        AdmobAdapter.initMobileAds(this)
        adView = AdView(this)
        ad_container.addView(adView)
        ad_container.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                adView.adSize = adaptiveAdSize
                adView.adUnitId = resources.getString(R.string.ad_unit_id_banner)
                AdmobAdapter.loadBannerAdMob(adView)
            }
        }
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

    /** Called when leaving the activity  */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
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
