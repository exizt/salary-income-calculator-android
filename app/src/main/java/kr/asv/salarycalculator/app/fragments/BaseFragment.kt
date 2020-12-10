package kr.asv.salarycalculator.app.fragments

import androidx.fragment.app.Fragment
import android.view.View

import kr.asv.salarycalculator.app.MainActivity

/**
 * BaseFragment
 * Created by EXIZT on 2016-04-08.
 */
abstract class BaseFragment : Fragment() {
    /**
     * 자기 자신 Fragment
     */
    private var fragmentView: View? = null

    /**
     * Fragment 에서 자신을 지정
     *
     * @param view View
     */
    protected fun setFragmentView(view: View) {
        fragmentView = view
    }

    /**
     * 액션바 타이틀 변경
     */
    protected fun setActionBarTitle(title: String) {
        val activity = (activity as MainActivity?)!!
        activity.setActionBarTitle(title)
    }
}
