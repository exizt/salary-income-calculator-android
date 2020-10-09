package kr.asv.apps.salarycalculator

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.MenuItem
import android.view.View
import kr.asv.apps.salarycalculator.activities.SettingsActivity
import kr.asv.apps.salarycalculator.fragments.NormalCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.QuickCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.TaxCalculatorFragment
import kr.asv.apps.salarycalculator.fragments.WordItemFragment


/**
 * 메인 액티비티 등에서 상단 좌측의 메뉴 네비게이션
 * Created by EXIZT on 2016-06-08.
 */
class NavigationItemFactory {
    companion object {
        /**
         * 네비게이션 메뉴 부분
         */
        fun onItemSelected(activity: FragmentActivity, item: MenuItem, backStack: Boolean): Boolean {
            val isAvailable: Boolean

            when (item.itemId) {
                R.id.nav_calculator_quick -> {
                    // 퀵계산
                    val fragment = QuickCalculatorFragment()
                    replaceFragments(activity, fragment, backStack)
                    isAvailable = true
                }
                R.id.nav_calculator_older -> {
                    //실 수령액 계산
                    val fragment = NormalCalculatorFragment()
                    replaceFragments(activity, fragment, backStack)
                    isAvailable = true
                }
                R.id.nav_calculator_tax -> {
                    // 세율 계산
                    val fragment = TaxCalculatorFragment()
                    replaceFragments(activity, fragment, backStack)
                    isAvailable = true
                }
                R.id.nav_settings -> {
                    // 환경 설정
                    activity.startActivity(Intent(activity, SettingsActivity::class.java))
                    isAvailable = true
                }
                R.id.nav_word_dictionary -> {
                    // 용어 사전
                    //activity.startActivity(Intent(activity, WordListActivity::class.java))
                    replaceFragments(activity, WordItemFragment(), backStack)
                    isAvailable = true
                }
                else -> isAvailable = false
            }

            /*
            액티비티 또는 프레그먼트 호출 후에 처리.
            navigationDrawer(메뉴부분) 을 close 하는 부분.
            해당 메뉴가 없을 시에는 SnackBar 호출
             */
            if (isAvailable) {
                val drawerLayout = activity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return isAvailable
        }

        /**
         * 프레그먼트 를 교체할 경우에 발생
         * @param activity 보통은 MainActivity 를 넘긴다. 그게 아닌 경우 일반 Activity 를 넘긴다.
         * @param fragment 프레그먼트 정보
         * @param backStack backStack 을 남길지 여부. true 인 경우는 backStack 히스토리에 남긴다.
         */
        private fun replaceFragments(activity: FragmentActivity, fragment: Fragment, backStack: Boolean = true) {
            val manager: FragmentManager = activity.supportFragmentManager

            val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)

            if (backStack) {
                fragmentTransaction.addToBackStack(null)//히스토리에 남긴다.
            }
            fragmentTransaction.commit()
        }
    }
}
