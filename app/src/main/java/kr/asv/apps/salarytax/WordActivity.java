package kr.asv.apps.salarytax;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import kr.asv.apps.salarytax.databases.TableWordDictionary;
import kr.asv.shhtaxmanager.R;

public class WordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TextView word_subject = (TextView) findViewById(R.id.word_subject);
        TextView word_explanation = (TextView) findViewById(R.id.word_explanation);
        TextView word_history = (TextView) findViewById(R.id.word_history);
        TextView word_process = (TextView) findViewById(R.id.word_process);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            long wordKey = extras.getLong("wordKey");

            //이제 값을 조회해온다.
            TableWordDictionary tableWordDictionary = Services.getInstance().getTableWordDictionary();
            TableWordDictionary.Record record = tableWordDictionary.getRow(wordKey);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(record.getSubject());
            }

            //word_subject.setText(record.getSubject());
            word_explanation.setText(record.getExplanation());
            word_history.setText(record.getHistory());
            word_process.setText(record.getProcess());

        }
    }
}
