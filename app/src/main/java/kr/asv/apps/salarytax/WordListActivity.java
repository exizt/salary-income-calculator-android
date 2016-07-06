package kr.asv.apps.salarytax;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import kr.asv.apps.salarytax.databases.TableWordDictionary;
import kr.asv.apps.salarytax.items.WordDictionaryAdapter;
import kr.asv.apps.salarytax.items.WordDictionaryItem;
import kr.asv.shhtaxmanager.R;

public class WordListActivity extends AppCompatActivity {
    private WordDictionaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("용어 사전");
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //adapter.add("item1");
        //adapter.add("item2");

        drawDictionary();
    }
    private void drawDictionary()
    {
        TableWordDictionary tableWordDictionary = Services.getInstance().getTableWordDictionary();
        //tableWordDictionary.test();;

        Cursor cur = tableWordDictionary.getList();

        ArrayList<WordDictionaryItem> items = new ArrayList<>();

        if (cur.moveToFirst()) {

            while (cur.isAfterLast() == false) {
                //Log.e("SHH",""+cur.getInt(cur.getColumnIndex("key")));
                WordDictionaryItem wItem = new WordDictionaryItem();
                wItem.setKey(cur.getInt(cur.getColumnIndex("key")));
                wItem.setId(cur.getString(cur.getColumnIndex("id")));
                wItem.setSubject(cur.getString(cur.getColumnIndex("subject")));
                wItem.setExplanation(cur.getString(cur.getColumnIndex("explanation")));
                wItem.setProcess(cur.getString(cur.getColumnIndex("process")));
                wItem.setHistory(cur.getString(cur.getColumnIndex("history")));

                items.add(wItem);

                cur.moveToNext();
            }
        }
        cur.close();

        adapter = new WordDictionaryAdapter(this,R.layout.listitem_word);
        adapter.setItemList(items);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"dd",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getBaseContext(),WordActivity.class);
                intent.putExtra("wordKey",adapter.getItemKey(position));
                startActivity(intent);
            }
        });
    }
}
