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
import kr.asv.apps.salarycalculator.activities.AppSettingsActivity
import kr.asv.apps.salarycalculator.fragments.*


/**
 * 메인 액티비티 등에서 상단 좌측의 메뉴 네비게이션
 */
class NavigationItemFactory {
    companion object {
        private const val defaultMenuId = R.id.nav_calculator_quick

        /**
         * 네비게이션 메뉴 선택시
         */
        fun onItemSelected(activity: FragmentActivity, item: MenuItem, backStack: Boolean): Boolean {
            val isAvailable: Boolean = onItemSelectedEvent(activity, item.itemId, backStack)

            // 해당 메뉴를 선택하고 fragment 전환이 이루어졌으므로 네비게이션을 close 한다.
            if (isAvailable) {
                val drawerLayout = activity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return isAvailable
        }

        /**
         * 메뉴 ID 와 변경될 것들의 처리
         */
        private fun onItemSelectedEvent(activity: FragmentActivity, itemId: Int, backStack: Boolean): Boolean {
            val isDone : Boolean

            when (itemId) {
                R.id.nav_calculator_quick -> {
                    val fragment = QuickCalculatorFragment()
                    replaceFragments(activity, fragment, backStack)
                    isDone = true
                }
                R.id.nav_calculator_tax -> {
                    val fragment = TaxCalculatorFragment()
                    replaceFragments(activity, fragment, backStack)
                    isDone = true
                }
                R.id.nav_settings2 -> {
                    activity.startActivity(Intent(activity, AppSettingsActivity::class.java))
                    isDone = true
                }
                R.id.nav_word_dictionary -> {
                    replaceFragments(activity, WordItemFragment(), backStack)
                    isDone = true
                }
                R.id.nav_about -> {
                    val fragment = AboutFragment()
                    replaceFragments(activity, fragment, backStack)
                    isDone = true
                }
                else -> isDone = false
            }
            return isDone
        }

        /**
         * default 로 로딩하는 fragment
         * navigation menu 의 특정 항목을 불러오게함.
         * 백스택 히스토리에는 기록하지 않는다.
         */
        fun onItemFirst(activity: FragmentActivity) {
            onItemSelectedEvent(activity, defaultMenuId, false)
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
