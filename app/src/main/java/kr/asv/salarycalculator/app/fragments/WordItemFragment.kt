package kr.asv.salarycalculator.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.Services
import kr.asv.salarycalculator.app.activities.WordPageActivity
import kr.asv.salarycalculator.app.databinding.FragmentDictionaryItemBinding
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
    private lateinit var adapter :TermItemRecyclerViewAdapter

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
            adapter = TermItemRecyclerViewAdapter(this)
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
     * @see ://developer.android.com/guide/topics/ui/layout/recyclerview
     * https://stackoverflow.com/questions/60423596/how-to-use-viewbinding-in-a-recyclerview-adapter
     */
    class TermItemRecyclerViewAdapter(private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<TermItemRecyclerViewAdapter.TermViewHolder>() {
        private var items: List<Term> = ArrayList()


        inner class TermViewHolder(private val itemBinding: FragmentDictionaryItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            //val binding : FragmentDictionaryListBinding? = null
            //private val textView: TextView = itemView.findViewById<View>(R.id.content) as TextView
            private var mItem: Term? = null

            init {
                itemView.setOnClickListener{
                    //mListener?.onListFragmentInteraction(mItem!!)
                    mItem?.let{
                        mListener?.onListFragmentInteraction(mItem!!)
                        //onClick(it)
                    }
                }
            }
            fun bind(item: Term){
                mItem = item
                itemBinding.content.text = item.name
            }
            //override fun toString(): String = super.toString() + " '" + itemBinding.content.text + "'"
        }

        /**
         * onCreateViewHolder
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
            val itemBinding = FragmentDictionaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TermViewHolder(itemBinding)
            //val view = LayoutInflater.from(parent.context)
            //        .inflate(R.layout.fragment_dictionary_item, parent, false)
            //return TermViewHolder(view)
        }

        /**
         *
         */
        override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
            holder.bind(items[position])
        }


        override fun getItemCount(): Int = items.size

        fun setItems(items: List<Term>){
            this.items = items
            notifyDataSetChanged()
        }

    }
}

interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(item: Term)
}