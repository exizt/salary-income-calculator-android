package kr.asv.apps.salarytax.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import kr.asv.apps.salarytax.Services
import kr.asv.shhtaxmanager.R
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*

class WordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        setSupportActionBar(toolbar)
        supportActionBar?.run{
            setDisplayHomeAsUpEnabled(true)
        }

        val extras = intent.extras

        if (extras != null) {
            val wordKey = extras.getLong("wordKey")

            //이제 값을 조회해온다.
            val tableWordDictionary = Services.getInstance().tableWordDictionary
            val record = tableWordDictionary.getRow(wordKey)

            val actionBar = supportActionBar
            actionBar?.setTitle(record.getSubject())

            //word_subject.setText(record.getSubject());
            word_explanation.text = record.getExplanation()
            word_history.text = record.getHistory()
            word_process.text = record.getProcess()

        }
    }
}
