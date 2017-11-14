package kr.asv.androidutils

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

/**
 * Admob 관련 클래스
 * Created by EXIZT on 2017-11-14.
 */
class AdmobAdapter{
    companion object {
        /**
         * 구글 광고 추가할 때에.
         */
        @Suppress("unused")
        fun loadBannerAdMob(mAdView: AdView) {
            mAdView.loadAd(newAdRequest())
        }

        /**
         * 구글 전면광고 추가할 때에.
         * @param context
         */
        @Suppress("unused")
        fun loadInterstitialAdMob(context: Context,adUnitId:String) {
            val interstitialAd = InterstitialAd(context)
            interstitialAd.adUnitId = adUnitId
            interstitialAd.loadAd(newAdRequest())
        }

        /**
         * 구글 광고의 adRequest 를 생성 및 반환
         * @return
         */
        @Suppress("unused","SpellCheckingInspection")
        private fun newAdRequest(): AdRequest {
            val builder = AdRequest.Builder()
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            //builder.addTestDevice("621CBEEDE09F6A5B37180A718E74C41C");// G pro code
            //builder.addTestDevice("2D81264572D2AB096C895509EDBD419F");// 확인 필요
            return builder.build()
        }
    }
}