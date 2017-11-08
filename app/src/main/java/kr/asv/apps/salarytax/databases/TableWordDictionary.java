package kr.asv.apps.salarytax.databases;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016-04-30.
 */
public class TableWordDictionary {
    /**
     * 데이터베이스 핸들러.
     */
    private boolean debug = false;
    private final String DEBUG_TAG = "devdebug";
    private final String TABLE_NAME = "word_dictionary";
    private SQLiteDatabase db;

    public TableWordDictionary(SQLiteDatabase db)
    {
        this.db = db;
    }

    public Cursor getList()
    {
        Cursor cur = db.query(TABLE_NAME,new String[] { "key","id","subject","explanation","process","history" }, null, null,null,null,null); //
        return cur;
    }

    /**
     *
     * @param key
     * @return
     */
    public Record getRow(long key)
    {
        Record record = new Record();
        Cursor cur = db.query(TABLE_NAME,new String[] { "key","id","subject","explanation","process","history" }, "key = ?", new String[] { String.valueOf(key) },null,null,null); //
        if(cur.moveToFirst())
        {
            record.setKey(cur.getInt(cur.getColumnIndex("key")));
            record.setId(cur.getString(cur.getColumnIndex("id")));
            record.setSubject(cur.getString(cur.getColumnIndex("subject")));
            record.setExplanation(cur.getString(cur.getColumnIndex("explanation")));
            record.setProcess(cur.getString(cur.getColumnIndex("process")));
            record.setHistory(cur.getString(cur.getColumnIndex("history")));
        }
        cur.close();
        return record;
    }

    public class Record {
        private int key;
        public String id;
        public String subject;
        public String explanation;
        public String process;
        public String history;

        public String toString()
        {
            return "DataItem{id=[" + id + "], subject=["+subject + "] explanation=[" + explanation + "], process=[" + process + "], history=[" + history + "]}";
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }

        public String getHistory() {
            return history;
        }

        public void setHistory(String history) {
            this.history = history;
        }
    }
}
