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
import kr.asv.apps.salarycalculator.fragments.NormalCalculatorFragment
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
