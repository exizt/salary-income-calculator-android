package kr.asv.apps.salarycalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kr.asv.androidutils.AdmobAdapter
import kr.asv.apps.salarycalculator.activities.SettingsActivity
import kr.asv.apps.salarycalculator.activities.WordListActivity
import kr.asv.apps.salarycalculator.fragments.NormalCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.QuickCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.TaxCalculatorFragment
import kr.asv.shhtaxmanager.R


/**
 * 기본으로 호출되는 메인 액티비티 클래스
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    /**
     * 가장 처음에 호출되는 create 메서드
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //네비게이션 드로워 셋팅
        onCreateNavigationDrawer()

        //기본 Fragment 지정
        //NavigationItemFactory.instance.onNavigationItemFirst(this)
	    onNavigationItemFirst()

        //Services 초기화 및 인스턴스 가져오기
        Services.getInstance(this)

        // Admob 호출
        AdmobAdapter.loadBannerAdMob(adView)
    }

    /**
     * 네비게이션 드로워 셋팅
     */
    private fun onCreateNavigationDrawer() {
        //네비게이션 바 기능
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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
	 * 처음 로딩할 첫번째 항목
	 */
	private fun onNavigationItemFirst() {
		//onNavigationItemSelected(nav_view.menu.getItem(1))

        //nav_view.menu.getItem(R.id.nav_calculator_quick)
        //nav_view.menu.findItem(R.id.nav_calculator_quick)

        onNavigationItemSelected(nav_view.menu.findItem(R.id.nav_calculator_quick))
	}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var isAction = false

        when (item.itemId) {
            R.id.nav_calculator_quick -> {
                // 퀵계산
                val fragment = QuickCalculatorFragment()
                replaceFragments(fragment, true)
                isAction = true
            }
            R.id.nav_calculator_older -> {
                //실 수령액 계산
                val fragment = NormalCalculatorFragment()
                replaceFragments(fragment, true)
                isAction = true
            }
            R.id.nav_calculator_tax -> {
                // 세율 계산
                val fragment = TaxCalculatorFragment()
                replaceFragments(fragment, true)
                isAction = true
            }
            R.id.nav_settings -> {
                // 환경 설정
                startActivity(Intent(this,SettingsActivity::class.java))
                isAction = true
            }
            R.id.nav_word_dictionary -> {
                // 용어 사전
                startActivity(Intent(this,WordListActivity::class.java))
                isAction = true
            }
        }

        /*
        액티비티 또는 프레그먼트 호출 후에 처리.
        navigationDrawer(메뉴부분) 을 close 하는 부분.
        해당 메뉴가 없을 시에는 SnackBar 호출
         */
        if (isAction) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //val view = currentFocus
            Snackbar.make(this.currentFocus, "준비중입니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    /**
     * 디버깅
     * @param msg
     */
    fun debug(msg: String) {
        Log.e("[EXIZT-DEBUG]", "[MainActivity]" + msg)
    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }
}
