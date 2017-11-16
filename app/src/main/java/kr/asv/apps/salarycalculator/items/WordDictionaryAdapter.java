package kr.asv.apps.salarycalculator.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.asv.apps.salarycalculator.R;

/**
 * 단어 사전 관련 클래스. 확인 필요함.
 * Created by EXIZT on 2016-04-30.
 */
public class WordDictionaryAdapter extends BaseAdapter {
	@SuppressWarnings("CanBeFinal")
	private int layout;
	@SuppressWarnings("CanBeFinal")
	private LayoutInflater inflater;
	private ArrayList<WordDictionaryItem> mList;


	public WordDictionaryAdapter(Context context, int layout) {
		this.layout = layout;
		this.inflater = LayoutInflater.from(context);
		mList = new ArrayList<>();
	}

	/**
	 * 리스트 객체 내의 item 의 갯수를 반환해주는 함수.
	 *
	 * @return int
	 */
	@Override
	public int getCount() {
		return mList.size();
	}

	/**
	 * 전달받은 position 의 위치에 해당하는 리스트 객체의 item 을 반환하는 함수
	 *
	 * @param position int
	 * @return Object
	 */
	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	/**
	 * 전달받은 position 의 위치에 해당하는 리스트 객체의 item 의 row id 를 반환해주는 함수
	 *
	 * @param position int
	 * @return long
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	public long getItemKey(int position) {
		return mList.get(position).getKey();
	}

	/**
	 * 화면에 출력하는 함수. Listview 에 출력되는 갯수만큼 반복호출된다.
	 *
	 * @param position    int
	 * @param convertView View
	 * @param parent      ViewGroup
	 * @return View
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//아마도 처음 출력시에는 convertView 가 null 이고, 재호출인 경우는 null 이 아닐 것으로 추측된다.
		if (convertView == null) {
			// view 가 null 일 경우 커스텀 레이아웃을 얻어옴
			//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, parent, false);
		}

		TextView subject = convertView.findViewById(R.id.word_subject);
		//TextView key = (TextView) convertView.findViewById(R.id.word_key);

		// 현재 position 에 맞는 값을 가져옴.
		WordDictionaryItem item = mList.get(position);

		subject.setText(item.getSubject());
		//key.setText("TEST");

		//LinearLayout item_layout = (LinearLayout)convertView.findViewById(R.id.item_layout);

		return convertView;
	}

	@SuppressWarnings("unused")
	public void add(WordDictionaryItem item) {
		mList.add(item);
	}

	public void setItemList(ArrayList<WordDictionaryItem> items) {
		mList = items;
	}
}
