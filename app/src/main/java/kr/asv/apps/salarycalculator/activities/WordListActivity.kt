package kr.asv.apps.salarycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.items.WordDictionaryAdapter
import kr.asv.apps.salarycalculator.items.WordDictionaryItem
import kr.asv.apps.salarycalculator.R
import java.util.*

class WordListActivity : AppCompatActivity() {
	private var adapter: WordDictionaryAdapter? = null
	private var isDebug = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_word_list)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)

		title = "용어 사전"
		drawDictionary()
	}

	/**
	 *
	 */
	@Suppress("UNUSED_ANONYMOUS_PARAMETER")
	private fun drawDictionary() {
		val tableWordDictionary = Services.getInstance().wordDictionaryTable
		val items = ArrayList<WordDictionaryItem>()
		try {
			val cur = tableWordDictionary.list
			if (cur.moveToFirst()) {

				while (!cur.isAfterLast) {
					val wItem = WordDictionaryItem()
					wItem.key = cur.getInt(cur.getColumnIndex("key"))
					wItem.id = cur.getString(cur.getColumnIndex("id"))
					wItem.subject = cur.getString(cur.getColumnIndex("subject"))
					wItem.explanation = cur.getString(cur.getColumnIndex("explanation"))
					wItem.process = cur.getString(cur.getColumnIndex("process"))
					wItem.history = cur.getString(cur.getColumnIndex("history"))

					items.add(wItem)

					cur.moveToNext()
				}
			}
			cur.close()
		} catch (e: Exception) {
			debug("[drawDictionary] 데이터 로딩 실패 ")
			debug(e.toString())
			throw e
		}

		adapter = WordDictionaryAdapter(this, R.layout.listitem_word)
		adapter!!.setItemList(items)

		val listView = findViewById<ListView>(R.id.listView)
		listView.adapter = adapter

		listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			//Toast.makeText(getApplicationContext(),"dd",Toast.LENGTH_LONG).show();
			val intent = Intent(baseContext, WordActivity::class.java)
			intent.putExtra("wordKey", adapter!!.getItemKey(position))
			startActivity(intent)
		}
	}

	/**
	 * 디버깅
	 * @param msg message
	 */
	fun debug(msg: String) {
		@Suppress("ConstantConditionIf")
		if (isDebug) {
			Log.e("[EXIZT-DEBUG]", "[WordListActivity]" + msg)
		}
	}

	@Suppress("unused")
	fun setDebug(debug: Boolean) {
		isDebug = debug
	}
}
