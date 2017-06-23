package kr.asv.shhtaxmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import kr.asv.apps.salarytax.NavigationItemFactory;
import kr.asv.apps.salarytax.Services;
import kr.asv.apps.salarytax.SettingsActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //네비게이션 바 기능
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideSoftKeyboard();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //네비게이션 바 안에서 메뉴항목 부분
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //기본 Fragment 지정
        NavigationItemFactory.getInstance().onNavigationItemFirst(this);

        //Services 초기화 및 인스턴스 가져오기
        Services services = Services.getInstanceWithInit(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavigationItemFactory.getInstance().onNavigationItemSelected(this,item);
        return true;
    }

    public void replaceFragments(Fragment fragment)
    {
        replaceFragments(fragment,true);
    }
    public void replaceFragments(Fragment fragment,Boolean backStack)
    {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container,fragment);

        if(backStack) {
            fragmentTransaction.addToBackStack(null);//히스토리에 남긴다.
        }
        fragmentTransaction.commit();

    }
    /**
     * 키보드 내리기
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void debug(String msg)
    {
        Log.e("[SHH DEBUG]", msg);
    }

    public void setActionBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }
    public String getAppVersion()
    {
        return BuildConfig.VERSION_NAME;
    }

}
