package kr.asv.apps.salarycalculator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.content_word.*
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services

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

			//이제 값을 조회해온다.
			val tableWordDictionary = Services.getInstance().wordDictionaryTable
			val record = tableWordDictionary.getRow(wordKey)

			val actionBar = supportActionBar
			actionBar?.title = record.subject

			//word_subject.setText(record.getSubject());
			word_explanation.text = record.explanation
			word_history.text = record.history
			word_process.text = record.process
		} else {
			Log.e("EXIST-DEBUG","extras is null")
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
			}
		}

		return super.onOptionsItemSelected(item)
	}
}
