package kr.asv.apps.salarycalculator

import android.content.Context
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kr.asv.androidutils.AdmobAdapter


/**
 * 기본으로 호출되는 메인 액티비티 클래스
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val isDebug = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    /**
     * 가장 처음에 호출되는 create 메서드
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // 네비게이션 셋팅
        onCreateNavigationDrawer() // 네비게이션 드로워 셋팅
        NavigationItemFactory.onItemFirst(this) // 첫번째 메뉴 호출

        //Services 초기화 및 인스턴스 가져오기
        Services.load(this)

        // Firebase Analytics 초기화
        firebaseAnalytics = Firebase.analytics

        // Admob 호출
        AdmobAdapter.loadBannerAdMob(adView)
    }

    /**
     * 네비게이션 드로워 셋팅
     */
    private fun onCreateNavigationDrawer() {
        //네비게이션 바 기능
        //val toggle = ActionBarDrawerToggle(
        //        this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        val toggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                hideSoftKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideSoftKeyboard()
            }
        }

        //drawer.setDrawerListener(toggle);//deprecated
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //네비게이션 바 안에서 메뉴항목 부분
        nav_view.setNavigationItemSelectedListener(this)
    }

    /**
     * drawer 형태이고 open 이라면 closeDrawer 호출
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * default 로 로딩하는 fragment
     * navigation menu 의 특정 항목을 불러오게함.
     * 백스택 히스토리에는 기록하지 않는다.
     */
    private fun onNavigationItemFirst() {
        NavigationItemFactory.onItemSelected(this, nav_view.menu.findItem(R.id.nav_calculator_quick), false)
    }

    /**
     * navigationDrawer 에서 item 을 선택했을 때 발생하는 메서드
     * 해당 항목이 없을 시에는 '준비중입니다' 가 뜨도록 처리
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (!NavigationItemFactory.onItemSelected(this, item, true)) {
            //Snackbar.make(this.currentFocus, "준비중입니다", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
        return true
    }

    @JvmOverloads
    fun replaceFragments(fragment: Fragment, backStack: Boolean = true) {
        val manager: FragmentManager = supportFragmentManager

        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)

        if (backStack) {
            fragmentTransaction.addToBackStack(null)//히스토리에 남긴다.
        }
        fragmentTransaction.commit()
    }

    /**
     * 키보드 내리기
     */
    fun hideSoftKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        if (isDebug) {
            Services.debugLog("IncomeTaxDao", msg)
        }
    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }
}
