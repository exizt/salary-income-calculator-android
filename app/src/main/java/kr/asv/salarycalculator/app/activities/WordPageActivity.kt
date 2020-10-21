package kr.asv.salarycalculator.app.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*
import kr.asv.salarycalculator.utils.AdmobAdapter
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.model.TermDictionary

/**
 * 용어 상세 정보를 보여주는 페이지 액티비티 이다.
 */
class WordPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        val extras = intent.extras
        if (extras != null) {
            val wordKey = extras.getInt("wordKey") //숫자값. 인덱스.
            val wordId = extras.getString("wordId") //cid 값. 문자열.
            //debug("Word ID" + wordId)
            //debug("Word KEY" + wordKey.toString())

            if (wordId == null) {
                initData(wordKey)
            } else if (wordId.isNotEmpty()) {
                initData(wordId)
            }
        } else {
            debug("extras is null")
        }

        // Admob 호출
        AdmobAdapter.loadBannerAdMob(adView)
    }

    private fun appendData(record: TermDictionary) {
        val actionBar = supportActionBar
        actionBar?.title = record.name
        //word_subject.setText(record.getSubject());
        word_explanation.text = record.description
        word_history.text = record.history
        word_process.text = record.process
    }

    /**
     * 숫자 id (id) 값을 받았을 때.
     */
    private fun initData(wordKey: Int) {
        val tableWordDictionary = Services.getTermDictionaryDao()
        val record = tableWordDictionary.getRow(wordKey)
        appendData(record)
    }

    /**
     * 문자열 id (cid) 값을 받았을 때.
     */
    private fun initData(wordId: String) {
        val tableWordDictionary = Services.getTermDictionaryDao()
        val record = tableWordDictionary.getRowFromCID(wordId)
        appendData(record)
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

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        Services.debugLog("WordPageActivity", msg)
    }
}
