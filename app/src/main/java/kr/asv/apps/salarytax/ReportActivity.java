package kr.asv.apps.salarytax;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import kr.asv.apps.salarytax.fragments.ReportInputFragment;
import kr.asv.apps.salarytax.fragments.ReportInsuranceFragment;
import kr.asv.apps.salarytax.fragments.ReportSummaryFragment;
import kr.asv.apps.salarytax.fragments.ReportTaxFragment;
import kr.asv.shhtaxmanager.R;

public class ReportActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setTitle("실수령액 조회 결과");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //로드하면서 결과값을 조회해온다.
        setResultReport();
    }

    /**
     * 네비게이션 백버튼 클릭시, 메인액티비티를 recreate 를 하는 것을 방지하려는 목적.
     * @return
     */
    @Override
    public Intent getSupportParentActivityIntent() {
    /*String from = getIntent().getExtras().getString("from");
    Intent newIntent = null;
    if(from.equals("MAIN")){
        newIntent = new Intent(this, MainActivity.class);
    }else if(from.equals("FAV")){
        newIntent = new Intent(this, FavoriteActivity.class);
    }

    return newIntent;*/
        finish();
        return null;
    }
    public void setResultReport()
    {
        //MainActivity mainActivity = (MainActivity)getSupportParentActivityIntent();

        //getBaseContext();
        //MainActivity main = (MainActivity)getApplicationContext();
        //Calculator calculator = main.getCalculator();
        //Calculator calculator = Services.getInstance().getCalculator();

    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return ReportSummaryFragment.newInstance();
                case 1:
                    return ReportInputFragment.newInstance();
                case 2:
                    return ReportInsuranceFragment.newInstance();
                case 3:
                    return ReportTaxFragment.newInstance();
            }
            return ReportSummaryFragment.newInstance();
        }

        /**
         * 전체 갯수인가? 보여질 갯수인가? 나중에 확인해봐야함
         * @return
         */
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "요약";
                case 1:
                    return "입력값";
                case 2:
                    return "4대보험";
                case 3:
                    return "세금";

            }
            return null;
        }
    }
}
