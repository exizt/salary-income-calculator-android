package kr.asv.apps.salarytax

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.View

import kr.asv.apps.salarytax.activities.SettingsActivity
import kr.asv.apps.salarytax.activities.WordListActivity
import kr.asv.apps.salarytax.fragments.OlderCalculatorFragment
import kr.asv.apps.salarytax.fragments.QuickCalculatorFragment
import kr.asv.apps.salarytax.fragments.TaxCalculatorFragment
import kr.asv.shhtaxmanager.MainActivity
import kr.asv.shhtaxmanager.R

/**
 * Created by Administrator on 2016-06-08.
 */
class NavigationItemFactory {

    fun onNavigationItemFirst(mainActivity: MainActivity) {
        val fragment = QuickCalculatorFragment()
        mainActivity.replaceFragments(fragment, false)
        val drawer = mainActivity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    fun onNavigationItemSelected(mainActivity: MainActivity, item: MenuItem): Boolean {
        val id = item.itemId
        var isAction = false

        if (id == R.id.nav_calculator_quick) {
            /*
            퀵 계산
             */
            val fragment = QuickCalculatorFragment()
            mainActivity.replaceFragments(fragment)
            isAction = true

        } else if (id == R.id.nav_calculator_older) {
            /*
            실수령액 계산
             */
            val fragment = OlderCalculatorFragment()
            mainActivity.replaceFragments(fragment)
            isAction = true

        } else if (id == R.id.nav_calculator_tax) {
            /*
            세율 계산
             */
            val fragment = TaxCalculatorFragment()
            mainActivity.replaceFragments(fragment)
            isAction = true
        } else if (id == R.id.nav_settings) {
            /*
            환경설정
             */
            mainActivity.startActivity(Intent(mainActivity, SettingsActivity::class.java))
            isAction = true
        } else if (id == R.id.nav_word_dictionary) {
            /*
            용어사전
             */
            mainActivity.startActivity(Intent(mainActivity, WordListActivity::class.java))
            isAction = true
        } else {
            isAction = false
        }

        if (isAction) {
            val drawer = mainActivity.findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val view = mainActivity.currentFocus
            Snackbar.make(view!!, "준비중입니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        return isAction
    }

    companion object {

        val instance = NavigationItemFactory()
    }
}
