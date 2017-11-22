package kr.asv.apps.salarycalculator.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import kr.asv.apps.salarycalculator.MainActivity;

/**
 * BaseFragment
 * Created by EXIZT on 2016-04-08.
 */
public abstract class BaseFragment extends Fragment {
	/**
	 * 자기 자신 Fragment
	 */
	private View fragmentView;//fragment view

	/**
	 * Fragment 에서 자신을 지정
	 *
	 * @param view View
	 */
	@SuppressWarnings("WeakerAccess")
	protected void setFragmentView(View view) {
		fragmentView = view;
	}

	/**
	 * findViewById 를 편하게 사용하기 위해서 생성
	 *
	 * @return View view
	 */
	@SuppressWarnings("WeakerAccess")
	protected View findViewById(int id) {
		return this.fragmentView.findViewById(id);
	}

	/**
	 * 키보드 내리기
	 */
	@SuppressWarnings("WeakerAccess")
	protected void hideSoftKeyboard() {
		MainActivity activity = (MainActivity) getActivity();
		assert activity != null;
		activity.hideSoftKeyboard();
	}

	/**
	 * 액션바 타이틀 변경
	 */
	@SuppressWarnings("SameParameterValue")
	protected void setActionBarTitle(String title) {
		MainActivity activity = (MainActivity) getActivity();
		assert activity != null;
		activity.setActionBarTitle(title);
	}
}
