package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.databases.WordDictionaryTable

class WordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        val extras = intent.extras
        if (extras != null) {
            val wordKey = extras.getInt("wordKey")
            val wordId = extras.getString("wordId")
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
    }

    private fun appendData(record: WordDictionaryTable.Companion.Record) {
        val actionBar = supportActionBar
        actionBar?.title = record.subject
        //word_subject.setText(record.getSubject());
        word_explanation.text = record.explanation
        word_history.text = record.history
        word_process.text = record.process
    }

    /**
     *
     */
    private fun initData(wordKey: Int) {
        val tableWordDictionary = Services.instance.wordDictionaryTable
        val record = tableWordDictionary!!.getRow(wordKey)
        appendData(record)
    }

    /**
     *
     */
    private fun initData(wordId: String) {
        val tableWordDictionary = Services.instance.wordDictionaryTable
        val record = tableWordDictionary!!.getRowFromId(wordId)
        appendData(record)
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 디버깅
     * @param msg
     */
    fun debug(msg: String) {
        Log.e("[EXIZT-DEBUG]", "[WordActivity]$msg")
    }
}
