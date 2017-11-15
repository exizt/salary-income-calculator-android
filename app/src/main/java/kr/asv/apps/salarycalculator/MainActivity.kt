package kr.asv.shhtaxmanager

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kr.asv.androidutils.AdmobAdapter
import kr.asv.apps.salarytax.NavigationItemFactory
import kr.asv.apps.salarytax.Services
import android.view.ViewGroup
import android.widget.Button
import com.crashlytics.android.Crashlytics




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //네비게이션 드로워 셋팅
        onCreateNavigationDrawer()

        //기본 Fragment 지정
        NavigationItemFactory.instance.onNavigationItemFirst(this)

        //Services 초기화 및 인스턴스 가져오기
        Services.getInstanceWithInit(this)

        // Admob 호출
        AdmobAdapter.loadBannerAdMob(adView)
    }

    /**
     * 네비게이션 드로워 셋팅
     */
    private fun onCreateNavigationDrawer() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        //네비게이션 바 기능
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /*
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideSoftKeyboard();
            }
            */
        }
        //drawer.setDrawerListener(toggle);//deprecated
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //네비게이션 바 안에서 메뉴항목 부분
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        NavigationItemFactory.instance.onNavigationItemSelected(this, item)
        return true
    }

    @JvmOverloads
    fun replaceFragments(fragment: Fragment, backStack: Boolean? = true) {
        val fragmentManager: FragmentManager
        fragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction
        fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)

        if (backStack!!) {
            fragmentTransaction.addToBackStack(null)//히스토리에 남긴다.
        }
        fragmentTransaction.commit()

    }

    /**
     * 키보드 내리기
     */
    fun hideSoftKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 디버깅
     * @param log
     */
    fun debug(log: String) {
        Log.e("[EXIZT-DEBUG]", "[MainActivity]" + log)
    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.setTitle(title)
    }
}
