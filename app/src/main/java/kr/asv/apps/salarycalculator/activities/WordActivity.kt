package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*
import kr.asv.apps.salarycalculator.Services
import kr.asv.shhtaxmanager.R

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
            val wordKey = extras.getLong("wordKey")

            //이제 값을 조회해온다.
            val tableWordDictionary = Services.getInstance().wordDictionaryTable
            val record = tableWordDictionary.getRow(wordKey)

            val actionBar = supportActionBar
            actionBar?.title = record.subject

            //word_subject.setText(record.getSubject());
            word_explanation.text = record.explanation
            word_history.text = record.history
            word_process.text = record.process

        }
    }
}
