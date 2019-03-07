package kr.asv.apps.salarycalculator.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.asv.apps.salarycalculator.Services
import kr.asv.apps.salarycalculator.activities.WordPageActivity
import kr.asv.apps.salarycalculator.fragments.dummy.WordDictionaryContent
import kr.asv.apps.salarycalculator.fragments.dummy.WordDictionaryContent.Item
import kr.asv.shhtaxmanager.R

/**
 * 형태 : 리스트 형 Fragment
 * 단어 목록 Fragment 클래스
 * xml : fragment_dictionary_list 와 연관됨.
 */
class WordItemFragment : BaseFragment(), OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debug("onCreate")
        getWordDictionaryListData()
    }

    /**
     * onCreateView
     */
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

    /**
     * item 을 넘겨 받았을 때의 액션
     * WordPageActivity 를 새로 호출.
     */
    override fun onListFragmentInteraction(item: Item) {
        val intent = Intent(activity, WordPageActivity::class.java)
        intent.putExtra("wordKey", item.id)
        startActivity(intent)
    }

    /**
     * 데이터베이스 에서 목록을 조회하는 메서드
     */
    private fun getWordDictionaryListData() {
        if (WordDictionaryContent.isUsed) return

        debug("getWordDictionaryListData")
        // 객체 를 가져오기만 함
        val table = Services.getTermDictionaryDao()
        try {
            // select 쿼리를 실행함
            val cur = table.list
            if (cur.moveToFirst()) {

                while (!cur.isAfterLast) {
                    //val wItem = WordDictionaryItem()
                    val item = Item()
                    item.id = cur.getInt(cur.getColumnIndex("id"))
                    item.cid = cur.getString(cur.getColumnIndex("cid"))
                    item.name = cur.getString(cur.getColumnIndex("name"))
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
            debug("[drawDictionary] 데이터 로딩 실패 ",e)
            throw e
        }
    }

    /**
     * 디버깅 메서드
     * 변수가 두개 넘어올 경우의 처리 추가
     * @param msg 메시지
     */
    @Suppress("unused")
    private fun debug(msg: String, msg2 : Any = "") {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            Log.d(TAG, "$msg $msg2")
        }
    }

    /**
     * Fragment_wordItem.xml 과 연관된 클래스
     */
    class MyWordItemRecyclerViewAdapter(private val mValues: List<WordDictionaryContent.Item>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyWordItemRecyclerViewAdapter.ViewHolder>() {

        /**
         * onCreateViewHolder
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_dictionary_item, parent, false)
            return ViewHolder(view)
        }

        /**
         *
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mItem = mValues[position]
            //holder.mIdView.text = mValues[position].id
            holder.mContentView.text = mValues[position].name

            holder.mView.setOnClickListener {
                // 목록에서 클릭을 말함. (컨텐츠가 아니다. 컨텐츠 프레그먼트는 따로 만드셔)
                mListener?.onListFragmentInteraction(holder.mItem!!)
            }
        }

        override fun getItemCount(): Int = mValues.size

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            //val mIdView: TextView = mView.findViewById<View>(R.id.id) as TextView
            val mContentView: TextView = mView.findViewById<View>(R.id.content) as TextView
            var mItem: WordDictionaryContent.Item? = null

            override fun toString(): String = super.toString() + " '" + mContentView.text + "'"
        }
    }
    companion object {
        private const val TAG = "[EXIZT-DEBUG][WordItemFragment]"
        private const val isDebug = false
        fun newInstance(): WordItemFragment = WordItemFragment()
    }
}

interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(item: Item)
}