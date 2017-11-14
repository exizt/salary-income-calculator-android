package kr.asv.apps.salarytax.activities

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView

import kr.asv.apps.salarytax.Services
import kr.asv.apps.salarytax.databases.TableWordDictionary
import kr.asv.shhtaxmanager.R

class WordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //TextView word_subject = (TextView) findViewById(R.id.word_subject);
        val word_explanation = findViewById<View>(R.id.word_explanation) as TextView
        val word_history = findViewById<View>(R.id.word_history) as TextView
        val word_process = findViewById<View>(R.id.word_process) as TextView
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
