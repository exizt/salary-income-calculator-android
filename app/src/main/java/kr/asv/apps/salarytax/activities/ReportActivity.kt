package kr.asv.apps.salarytax.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import kr.asv.apps.salarytax.fragments.ReportInputFragment
import kr.asv.apps.salarytax.fragments.ReportInsuranceFragment
import kr.asv.apps.salarytax.fragments.ReportSummaryFragment
import kr.asv.apps.salarytax.fragments.ReportTaxFragment
import kr.asv.shhtaxmanager.R

class ReportActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        title = "실수령액 조회 결과"

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById<View>(R.id.container)
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        //로드하면서 결과값을 조회해온다.
        setResultReport()
    }

    /**
     * 네비게이션 백버튼 클릭시, 메인액티비티를 recreate 를 하는 것을 방지하려는 목적.
     * @return
     */
    override fun getSupportParentActivityIntent(): Intent? {
        /*String from = getIntent().getExtras().getString("from");
    Intent newIntent = null;
    if(from.equals("MAIN")){
        newIntent = new Intent(this, MainActivity.class);
    }else if(from.equals("FAV")){
        newIntent = new Intent(this, FavoriteActivity.class);
    }

    return newIntent;*/
        finish()
        return null
    }

    fun setResultReport() {
        //MainActivity mainActivity = (MainActivity)getSupportParentActivityIntent();

        //getBaseContext();
        //MainActivity main = (MainActivity)getApplicationContext();
        //Calculator calculator = main.getCalculator();
        //Calculator calculator = Services.getInstance().getCalculator();

    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return ReportSummaryFragment.newInstance()
                1 -> return ReportInputFragment.newInstance()
                2 -> return ReportInsuranceFragment.newInstance()
                3 -> return ReportTaxFragment.newInstance()
            }
            return ReportSummaryFragment.newInstance()
        }

        /**
         * 전체 갯수인가? 보여질 갯수인가? 나중에 확인해봐야함
         * @return
         */
        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "요약"
                1 -> return "입력값"
                2 -> return "4대보험"
                3 -> return "세금"
            }
            return null
        }
    }
}
