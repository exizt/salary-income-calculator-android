package kr.asv.salarycalculator.app.fragments

import androidx.fragment.app.Fragment
import kr.asv.salarycalculator.app.MainActivity

/**
 * BaseFragment
 * Created by EXIZT on 2016-04-08.
 */
abstract class BaseFragment : Fragment() {

    /**
     * 액션바 타이틀 변경
     */
    protected fun setActionBarTitle(title: String) {
        val activity = (activity as MainActivity?)!!
        activity.setActionBarTitle(title)
    }
}
