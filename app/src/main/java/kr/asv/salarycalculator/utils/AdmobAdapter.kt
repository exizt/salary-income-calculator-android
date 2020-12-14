package kr.asv.salarycalculator.utils

import android.content.Context
import android.provider.Settings
import com.google.android.gms.ads.*

/**
 * AdMob 관련 클래스
 * Created by EXIZT on 2017-11-14.
 */
class AdmobAdapter {
    companion object {
        /**
         * AdMob 의 initialize
         */
        fun initMobileAds(context: Context){
            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(context) { }

            // TestDeviceID 지정 및 Build
            MobileAds.setRequestConfiguration(
                    RequestConfiguration.Builder()
                            .setTestDeviceIds(getTestDeviceIds())
                            .build()
            )
        }

        /**
         * 구글 배너 광고 추가할 때에 사용하는 메서드
         */
        @Suppress("unused")
        fun loadBannerAdMob(mAdView: AdView) {
            //val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            //MobileAds.setRequestConfiguration(configuration)

            if(isValid(mAdView.context)){
                mAdView.loadAd(AdRequest.Builder().build())
            }
        }

        /**
         * 테스트 장치 아이디 목록
         * 에뮬레이터는 추가 안 해도 됨.
         */
        @Suppress("SpellCheckingInspection")
        private fun getTestDeviceIds(): List<String>{
            @Suppress("UnnecessaryVariable")
            val testDeviceIds = mutableListOf<String>()
            testDeviceIds.add("2D81264572D2AB096C895509EDBD419F") // Samsung G3 테스트 기기
            //testDeviceIds.add("621CBEEDE09F6A5B37180A718E74C41C") // G pro 테스트 기기
            return testDeviceIds
        }

        /**
         * 광고가 동작할 환경인지 분기문
         * '사전 출시 보고서'에서는 false
         */
        private fun isValid(context: Context): Boolean{
            return (!isTestLabEmulator(context))
            //return false
        }

        /**
         * 구글 Test Lab 에서 멍청하게 자꾸 광고를 클릭하기 때문에 추가한 메서드.
         * 나중에 구글 Test Lab 서비스가 똑똑해지면 필요하지 않게 될 예정. (가능하려나?)
         */
        private fun isTestLabEmulator(context: Context): Boolean {
            val testLabSetting = Settings.System.getString(context.contentResolver, "firebase.test.lab")
            return "true" == testLabSetting
        }
    }
}