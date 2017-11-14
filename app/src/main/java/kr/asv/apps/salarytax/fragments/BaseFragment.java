package kr.asv.apps.salarytax.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import kr.asv.shhtaxmanager.MainActivity;

/**
 * BaseFragment
 * Created by Administrator on 2016-04-08.
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 자기 자신 Fragment
     */
    protected View fragmentView;//fragment view

    /**
     * Fragment 에서 자신을 지정
     * @param view
     */
    public void setFragmentView(View view)
    {
        fragmentView = view;
    }
    /**
     * findViewById 를 편하게 사용하기 위해서 생성
     * @return View
     * @author hong seok-hoon
     */
    public View findViewById(int id)
    {
        return this.fragmentView.findViewById(id);
    }

    /**
     * 키보드 내리기
     */
    protected void hideSoftKeyboard() {
        MainActivity activity = (MainActivity)getActivity();
        activity.hideSoftKeyboard();
    }

    /**
     * 액션바 타이틀 변경
     */
    protected void setActionBarTitle(String title) {
        MainActivity activity = (MainActivity)getActivity();
        activity.setActionBarTitle(title);
    }
}
