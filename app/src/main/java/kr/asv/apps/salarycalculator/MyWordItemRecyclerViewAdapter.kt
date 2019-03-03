package kr.asv.apps.salarycalculator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kr.asv.apps.salarycalculator.fragments.OnListFragmentInteractionListener
import kr.asv.apps.salarycalculator.fragments.dummy.WordDictionaryContent

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
