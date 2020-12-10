package kr.asv.salarycalculator.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.activities.WordPageActivity
import kr.asv.salarycalculator.app.model.Term
import kr.asv.salarycalculator.app.model.TermViewModel

/**
 * 형태 : 리스트 형 Fragment
 * 단어 목록 Fragment 클래스
 * xml : fragment_dictionary_list 와 연관됨.
 */
class WordItemFragment : BaseFragment(), OnListFragmentInteractionListener {
    private val isDebug = false
    private lateinit var termViewModel: TermViewModel
    private lateinit var adapter :MyWordItemRecyclerViewAdapter

    /**
     * onCreate
     */
    @Suppress("RedundantOverride")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //debug("onCreate")
        //getWordDictionaryListData()
    }

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //debug("onCreateView")
        val view = inflater.inflate(R.layout.fragment_dictionary_list, container, false)

        setActionBarTitle(resources.getString(R.string.nav_menu_word_dictionary))

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            adapter = MyWordItemRecyclerViewAdapter(this)
            val recyclerView: RecyclerView = view
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            termViewModel = ViewModelProvider(this).get(TermViewModel::class.java)
            termViewModel.getAll().observe(viewLifecycleOwner, { t -> adapter.setItems(t!!)})

            /*
            lifecycleScope.launch {
                termViewModel.getAll()
            }
            */
        }
        return view
    }

    /**
     * item 을 넘겨 받았을 때의 액션
     * WordPageActivity 를 새로 호출.
     */
    override fun onListFragmentInteraction(item: Term) {
        val intent = Intent(activity, WordPageActivity::class.java)
        intent.putExtra("termId", item.id)
        startActivity(intent)
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        if (isDebug) {
            Services.debugLog("WordItemFragment", msg)
        }
    }


    /**
     * Fragment_wordItem.xml 과 연관된 클래스
     */
    class MyWordItemRecyclerViewAdapter(private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyWordItemRecyclerViewAdapter.ViewHolder>() {
        private var items: List<Term> = ArrayList()

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
            holder.mItem = items[position]
            //holder.mIdView.text = terms[position].id
            holder.textView.text = items[position].name

            holder.mView.setOnClickListener {
                // 목록에서 클릭을 말함. (컨텐츠가 아니다. 컨텐츠 프레그먼트는 따로 만드셔)
                mListener?.onListFragmentInteraction(holder.mItem!!)
            }
        }


        override fun getItemCount(): Int = items.size

        fun setItems(items: List<Term>){
            //Log.d("[EXIZT-SCalculator]", "MyWordItemRecyclerViewAdapter.setItems")
            this.items = items
            notifyDataSetChanged()
        }

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            //val mIdView: TextView = mView.findViewById<View>(R.id.id) as TextView
            
            val textView: TextView = mView.findViewById<View>(R.id.content) as TextView
            var mItem: Term? = null

            override fun toString(): String = super.toString() + " '" + textView.text + "'"
        }
    }
}

interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(item: Term)
}