package kr.asv.salarycalculator.app.activities

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.databinding.ActivityWordBinding
import kr.asv.salarycalculator.app.model.Term
import kr.asv.salarycalculator.app.model.TermViewModel
import kr.asv.salarycalculator.utils.AdmobAdapter

/**
 * 용어 상세 정보를 보여주는 페이지 액티비티 이다.
 */
class WordPageActivity : AppCompatActivity() {
    private val isDebug = false
    private lateinit var termViewModel: TermViewModel
    private lateinit var toolbarLayout: CollapsingToolbarLayout
    private lateinit var binding: ActivityWordBinding

    // AdView 관련
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private val adaptiveAdSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordBinding.inflate(layoutInflater)

        //setContentView(R.layout.activity_word)
        val view = binding.root
        setContentView(view)

        //setSupportActionBar(toolbar)
        setSupportActionBar(binding.toolbar)

        toolbarLayout = findViewById(R.id.toolbar_layout)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        termViewModel = ViewModelProvider(this).get(TermViewModel::class.java)

        val extras = intent.extras
        if (extras != null) {
            val termCid = extras.getString("termCid") //cid 값. 문자열
            val termId = extras.getInt("termId") //숫자값. Int. 값이 안 넘어와도 0으로 넘어옴에 주의.
            debug("Word ID [$termId]")
            debug("Word KEY [$termCid]")

            if(termCid != null && termCid.isNotEmpty()) {
                initData(termCid)
            } else {
                initData(termId)
            }

        } else {
            finishDebug("extras is null")
        }

        // Admob 호출
        //AdmobAdapter.loadBannerAdMob(adView)
        AdmobAdapter.initMobileAds(this)
        adView = AdView(this)
        binding.adContainer.addView(adView)
        binding.adContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                adView.adSize = adaptiveAdSize
                adView.adUnitId = resources.getString(R.string.ad_unit_id_banner)
                AdmobAdapter.loadBannerAdMob(adView)
            }
        }
    }

    private fun finishDebug(msg: Any?){
        debug(msg.toString())
        finish()
    }

    /**
     * 숫자 id (id) 값을 받았을 때.
     */
    private fun initData(termId: Int) {
        termViewModel.findById(termId).observe(this,{ t-> appendData(t)})

        //val tableWordDictionary = Services.getTermDictionaryDao()
        //val record = tableWordDictionary.getRow(wordKey)
        //appendData(record)
    }

    /**
     * 문자열 id (cid) 값을 받았을 때.
     */
    private fun initData(termCid: String) {
        termViewModel.findByCid(termCid).observe(this,{ t-> appendData(t)})

        //val tableWordDictionary = Services.getTermDictionaryDao()
        //val record = tableWordDictionary.getRowFromCID(wordId)
        //appendData(record)
    }

    /**
     * 데이터를 보여줌
     */
    private fun appendData(item: Term?) {

        if(item == null){
            finishDebug("데이터가 없음.")
        }

        //val actionBar = supportActionBar
        //actionBar?.title = item.name

        //CollapsingToolbarLayout toolbarLayout = (findViewById(R.id.toolbar_layout)) as CollapsingToolbarLayout
        //val toolbarLayout = toolbar_layout as CollapsingToolbarLayout
        //val toolbar = findViewById<View>(R.id.toolbar)
        toolbarLayout.title = item?.name

        //CollapsingToolbarLayout toolbarLayout = toolbar_layout

        //word_subject.setText(record.getSubject());

        //word_explanation.text = item?.description
        //word_history.text = item?.history
        //word_process.text = item?.process

        val layout = binding.includedLayout
        layout.wordExplanation.text = item?.description
        layout.wordHistory.text = item?.history
        layout.wordProcess.text = item?.process
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        if (isDebug) {
            Services.debugLog("WordPageActivity", msg)
        }
    }
}
