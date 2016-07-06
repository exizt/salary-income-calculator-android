package kr.asv.apps.salarytax;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import kr.asv.apps.salarytax.fragments.OlderCalculatorFragment;
import kr.asv.apps.salarytax.fragments.QuickCalculatorFragment;
import kr.asv.apps.salarytax.fragments.TaxCalculatorFragment;
import kr.asv.shhtaxmanager.MainActivity;
import kr.asv.shhtaxmanager.R;

/**
 * Created by Administrator on 2016-06-08.
 */
public class NavigationItemFactory {

    private static NavigationItemFactory instance = new NavigationItemFactory();
    public static NavigationItemFactory getInstance() {
        return instance;
    }

    public void onNavigationItemFirst(MainActivity mainActivity)
    {
        QuickCalculatorFragment fragment = new QuickCalculatorFragment();
        mainActivity.replaceFragments(fragment,false);
        DrawerLayout drawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MainActivity mainActivity, MenuItem item) {
        int id = item.getItemId();
        boolean isAction = false;

        if (id == R.id.nav_calculator_quick) {
            /*
            퀵 계산
             */
            QuickCalculatorFragment fragment = new QuickCalculatorFragment();
            mainActivity.replaceFragments(fragment);
            isAction = true;

        } else if (id == R.id.nav_calculator_older) {
            /*
            실수령액 계산
             */
            OlderCalculatorFragment fragment = new OlderCalculatorFragment();
            mainActivity.replaceFragments(fragment);
            isAction = true;

        } else if (id == R.id.nav_calculator_tax) {
            /*
            세율 계산
             */
            TaxCalculatorFragment fragment = new TaxCalculatorFragment();
            mainActivity.replaceFragments(fragment);
            isAction = true;
        } else if (id == R.id.nav_settings) {
            /*
            환경설정
             */
            mainActivity.startActivity(new Intent(mainActivity,SettingsActivity.class));
            isAction = true;
        } else if (id == R.id.nav_word_dictionary) {
            /*
            용어사전
             */
            mainActivity.startActivity(new Intent(mainActivity,WordListActivity.class));
            isAction = true;
        } else {
            isAction = false;
        }

        if(isAction) {
            DrawerLayout drawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else {
            View view = mainActivity.getCurrentFocus();
            Snackbar.make(view, "준비중입니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return isAction;
    }
}
