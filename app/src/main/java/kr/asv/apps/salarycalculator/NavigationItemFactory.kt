package kr.asv.apps.salarycalculator

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.View

import kr.asv.apps.salarycalculator.activities.SettingsActivity
import kr.asv.apps.salarycalculator.activities.WordListActivity
import kr.asv.apps.salarycalculator.fragments.OlderCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.QuickCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.TaxCalculatorFragment
import kr.asv.shhtaxmanager.R

/**
 * 메인 액티비티 등에서 상단 좌측의 메뉴 네비게이션
 * Created by EXIZT on 2016-06-08.
 */
class NavigationItemFactory {

    /**
     * 처음 로딩할 첫번째 항목
     */
    fun onNavigationItemFirst(fragmentActivity: FragmentActivity) {
        val fragment = QuickCalculatorFragment()
        replaceFragments(fragmentActivity, fragment, false)
        val drawer = fragmentActivity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    /*
    * 메뉴에서 항목을 선택한 경우에 발생
    * @param fragmentActivity
    * @param item
    * @return
    */
    fun onNavigationItemSelected(fragmentActivity: FragmentActivity, item: MenuItem): Boolean {
        val id = item.itemId
        var isAction = false

        when (id) {
            R.id.nav_calculator_quick -> {
                // 퀵계산
                val fragment = QuickCalculatorFragment()
                replaceFragments(fragmentActivity, fragment, true)
                isAction = true
            }
            R.id.nav_calculator_older -> {
                //실 수령액 계산
                val fragment = OlderCalculatorFragment()
                replaceFragments(fragmentActivity, fragment, true)
                isAction = true
            }
            R.id.nav_calculator_tax -> {
                // 세율 계산
                val fragment = TaxCalculatorFragment()
                replaceFragments(fragmentActivity, fragment, true)
                isAction = true
            }
            R.id.nav_settings -> {
                // 환경 설정
                fragmentActivity.startActivity(Intent(fragmentActivity, SettingsActivity::class.java))
                isAction = true
            }
            R.id.nav_word_dictionary -> {
                // 용어 사전
                fragmentActivity.startActivity(Intent(fragmentActivity, WordListActivity::class.java))
                isAction = true
            }
        }

        /*
        액티비티 또는 프레그먼트 호출 후에 처리.
        navigationDrawer(메뉴부분) 을 close 하는 부분.
        해당 메뉴가 없을 시에는 SnackBar 호출
         */
        if (isAction) {
            val drawer = fragmentActivity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val view = fragmentActivity.currentFocus
            Snackbar.make(view!!, "준비중입니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        return isAction
    }


    /**
     * 프레그먼트 를 교체할 경우에 발생
     * @param fragmentActivity 보통은 MainActivity 를 넘긴다. 그게 아닌 경우 일반 Activity 를 넘긴다.
     * @param fragment 프레그먼트 정보
     * @param backStack backStack 을 남길지 여부. true 인 경우는 backStack 히스토리에 남긴다.
     */
    private fun replaceFragments(fragmentActivity: FragmentActivity, fragment: Fragment, backStack: Boolean?) {
        val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager

        val fragmentTransaction: FragmentTransaction
        fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container, fragment)

        if (backStack!!) {
            fragmentTransaction.addToBackStack(null)//히스토리에 남긴다.
        }
        fragmentTransaction.commit()
    }

    companion object {

        val instance = NavigationItemFactory()
    }
}
