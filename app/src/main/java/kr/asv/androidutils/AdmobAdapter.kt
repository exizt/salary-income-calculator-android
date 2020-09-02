package kr.asv.androidutils

import android.content.Context
import android.provider.Settings
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

/**
 * AdMob 관련 클래스
 * Created by EXIZT on 2017-11-14.
 */
class AdmobAdapter {
    companion object {
        /**
         * 구글 배너 광고 추가할 때에 사용하는 메서드
         */
        @Suppress("unused")
        fun loadBannerAdMob(mAdView: AdView) {
            if(!isTestDevice(mAdView.context)){
                mAdView.loadAd(newAdRequest())
            }
        }

        /**
         * 구글 전면광고 추가할 때에.
         * @param context
         */
        @Suppress("unused")
        fun loadInterstitialAdMob(context: Context, adUnitId: String) {
            val interstitialAd = InterstitialAd(context)
            interstitialAd.adUnitId = adUnitId
            interstitialAd.loadAd(newAdRequest())
        }

        /**
         * 구글 광고의 adRequest 를 생성 및 반환
         * @return
         */
        @Suppress("unused", "SpellCheckingInspection")
        private fun newAdRequest(): AdRequest {
            val builder = AdRequest.Builder()
            //builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // 이제는 에뮬레이터는 기본으로 지정되어 있으니, 넣으면 오히려 에러나는 구문.
            //builder.addTestDevice("621CBEEDE09F6A5B37180A718E74C41C");// G pro 테스트 기기
            //builder.addTestDevice("2D81264572D2AB096C895509EDBD419F");// Samsung G3 테스트 기기
            return builder.build()
        }

        /**
         * 구글 Test Lab 에서 멍청하게 자꾸 광고를 클릭하기 때문에 추가한 메서드.
         * 나중에 구글 Test Lab 서비스가 똑똑해지면 필요하지 않게 될 예정. (가능하려나?)
         */
        private fun isTestDevice(context: Context): Boolean {
            val testLabSetting = Settings.System.getString(context.contentResolver, "firebase.test.lab")
            return "true" == testLabSetting
        }
    }
}