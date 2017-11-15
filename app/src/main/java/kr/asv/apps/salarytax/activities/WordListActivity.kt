package kr.asv.apps.salarytax.activities

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import java.util.ArrayList

import kr.asv.apps.salarytax.Services
import kr.asv.apps.salarytax.databases.TableWordDictionary
import kr.asv.apps.salarytax.items.WordDictionaryAdapter
import kr.asv.apps.salarytax.items.WordDictionaryItem
import kr.asv.shhtaxmanager.R

class WordListActivity : AppCompatActivity() {
    private var adapter: WordDictionaryAdapter? = null
    private val isDebug = false

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
    private fun drawDictionary() {
        val tableWordDictionary = Services.getInstance().tableWordDictionary
        //tableWordDictionary.test();;
        val items = ArrayList<WordDictionaryItem>()
        try {
            val cur = tableWordDictionary.list
            if (cur.moveToFirst()) {

                while (!cur.isAfterLast) {
                    //Log.e("SHH",""+cur.getInt(cur.getColumnIndex("key")));
                    val wItem = WordDictionaryItem()
                    wItem.key = cur.getInt(cur.getColumnIndex("key"))
                    wItem.setId(cur.getString(cur.getColumnIndex("id")))
                    wItem.setSubject(cur.getString(cur.getColumnIndex("subject")))
                    wItem.setExplanation(cur.getString(cur.getColumnIndex("explanation")))
                    wItem.setProcess(cur.getString(cur.getColumnIndex("process")))
                    wItem.setHistory(cur.getString(cur.getColumnIndex("history")))

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
        if (isDebug) {
            Log.e("[EXIZT-DEBUG]", "[WordListActivity]" + msg)
        }
    }
}
