package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.asv.apps.salarycalculator.MyWordItemRecyclerViewAdapter
import kr.asv.apps.salarycalculator.R
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.WordActivity
import kr.asv.apps.salarycalculator.fragments.dummy.WordDictionaryContent
import kr.asv.apps.salarycalculator.fragments.dummy.WordDictionaryContent.Item


/**
 * 형태 : 리스트 형 Fragment
 * 단어 목록 Fragment 클래스
 * xml : fragment_dictionary_list 와 연관됨.
 */
class WordItemFragment : BaseFragment(), OnListFragmentInteractionListener {
	private val isDebug = true

	override fun onListFragmentInteraction(item: Item) {
		val intent = Intent(activity, WordActivity::class.java)
		intent.putExtra("wordKey", item.key)
		startActivity(intent)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		debug("onCreate")
		getDictionaryData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_dictionary_list, container, false)

		setActionBarTitle(resources.getString(R.string.nav_menu_word_dictionary))

		// Set the adapter
		if (view is RecyclerView) {
			val context = view.getContext()
			val recyclerView: RecyclerView = view
			recyclerView.layoutManager = LinearLayoutManager(context)
			recyclerView.adapter = MyWordItemRecyclerViewAdapter(WordDictionaryContent.ITEMS, this)
		}
		return view
	}

	private fun getDictionaryData() {
		if(WordDictionaryContent.isUsed) return

		debug("getDictionaryData")
		// 객체 를 가져오기만 함
		val table = Services.getInstance().wordDictionaryTable
		try {
			// select 쿼리를 실행함
			val cur = table.list
			if (cur.moveToFirst()) {

				while (!cur.isAfterLast) {
					//val wItem = WordDictionaryItem()
					val item = Item()
					item.key = cur.getInt(cur.getColumnIndex("key"))
					item.id = cur.getString(cur.getColumnIndex("id"))
					item.subject = cur.getString(cur.getColumnIndex("subject"))
					//item.explanation = cur.getString(cur.getColumnIndex("explanation"))
					//item.process = cur.getString(cur.getColumnIndex("process"))
					//item.history = cur.getString(cur.getColumnIndex("history"))
					WordDictionaryContent.addItem(item)
					// cursor move
					cur.moveToNext()
				}
			}
			cur.close()
		} catch (e: Exception) {
			debug("[drawDictionary] 데이터 로딩 실패 ")
			debug(e.toString())
			throw e
		}
	}

	/**
	 * 디버깅
	 * @param msg message
	 */
	fun debug(msg: String) {
		@Suppress("ConstantConditionIf")
		if (isDebug) {
			Log.e("[EXIZT-DEBUG]", "[WordItemFragment]" + msg)
		}
	}

	companion object {
		fun newInstance(): WordItemFragment = WordItemFragment()
	}
}
interface OnListFragmentInteractionListener {
	fun onListFragmentInteraction(item: Item)
}