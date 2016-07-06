package kr.asv.apps.salarytax.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.asv.shhtaxmanager.R;

/**
 * Created by Administrator on 2016-04-30.
 */
public class WordDictionaryAdapter extends BaseAdapter {
    Context context;
    private int layout;
    private LayoutInflater inflater;
    private ArrayList<WordDictionaryItem> mList;


    public WordDictionaryAdapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        this.inflater = LayoutInflater.from(context);
        mList = new ArrayList<>();

    }
    /**
     * 리스트 객체 내의 item 의 갯수를 반환해주는 함수.
     * @return
     */
    @Override
    public int getCount() {
        return mList.size();
    }
    /**
     * 전달받은 position 의 위치에 해당하는 리스트 객체의 item 을 반환하는 함수
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }
    /**
     * 전달받은 position 의 위치에 해당하는 리스트 객체의 item 의 row id 를 반환해주는 함수
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    public long getItemKey(int position){
        return mList.get(position).getKey();
    }

    /**
     * 화면에 출력하는 함수. Listview 에 출력되는 갯수만큼 반복호출된다.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //아마도 처음 출력시에는 convertView 가 null 이고, 재호출인 경우는 null 이 아닐 것으로 추측된다.
        if (convertView == null) {
            // view 가 null 일 경우 커스텀 레이아웃을 얻어옴
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent,false);
        }

        TextView subject = (TextView) convertView.findViewById(R.id.word_subject);
        //TextView key = (TextView) convertView.findViewById(R.id.word_key);

        // 현재 position 에 맞는 값을 가져옴.
        WordDictionaryItem item = mList.get(position);

        subject.setText(item.getSubject());
        //key.setText("TEST");

        //LinearLayout item_layout = (LinearLayout)convertView.findViewById(R.id.item_layout);

        return convertView;
    }
    public void add(WordDictionaryItem item)
    {
        mList.add(item);
    }

    public void setItemList(ArrayList<WordDictionaryItem> items)
    {
        mList = items;
    }
}
