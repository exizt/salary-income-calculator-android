package kr.asv.shhtaxmanager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import kr.asv.apps.salarytax.Services;

public class IntroActivity extends AppCompatActivity {
    private Handler mHandler;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //인터넷 연결된 상태에서 load 애드몹 실행
        if(isNetworkAvailable()){
            loadAdmobInterstitial();
        } else {
            //연결되지 않았을 경우, 몇 초 뒤에 다시 시도
            mHandler = new Handler(); //딜래이를 주기 위해 핸들러 생성
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()) {
                        loadAdmobInterstitial();
                    } else {
                        //최종적으로 인터넷 연결이 안 된 상태... 광고 없이 진행하자.
                        closeIntro();
                    }
                }
            }, 5000); // 딜레이 ( 런어블 객체는 mrun, 시간 2초)
        }

        //Services 초기화 및 인스턴스 가져오기
        Services services = Services.getInstanceWithInit(getApplicationContext());
    }
    public void closeIntro()
    {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * 풀 스크린 광고 출력
     */
    private void loadAdmobInterstitial()
    {
        loadAdmobInterstitial(R.string.ad_interstitial_unit_id);
    }

    /**
     * Admob Interstitial 광고 호출
     * @param adUnitStringId
     */
    private void loadAdmobInterstitial(int adUnitStringId)
    {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(adUnitStringId));

        /*
         * 621CBEEDE09F6A5B37180A718E74C41C : LG G PRO - Test device
         * AdRequest.DEVICE_ID_EMULATOR : 모든 에뮬레이터. Avd 를 말하는 듯.
         */
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("621CBEEDE09F6A5B37180A718E74C41C").build();

        /*
         * AdListener 설정
         */
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                closeIntro();
            }
        });

        // Loading..
        mInterstitialAd.loadAd(adRequest);
    }
}
