package kr.asv.apps.salarycalculator.databases;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 단어 사전 테이블 클래스
 * Created by EXIZT on 2016-04-30.
 */
public class WordDictionaryTable {
	private boolean isDebug = false;
	@SuppressWarnings("FieldCanBeLocal")
	private final String DEBUG_TAG = "EXIZT-DEBUG";
	private final String TABLE_NAME = "word_dictionary";
	private final SQLiteDatabase db;

	public WordDictionaryTable(SQLiteDatabase db) {
		debug("Constructor..");
		this.db = db;
	}

	public Cursor getList() {
		debug("query");
		return db.query(TABLE_NAME, new String[]{"key", "id", "subject", "explanation", "process", "history"}, null, null, null, null, null);
	}

	/**
	 * @param key long
	 * @return Record
	 */
	public Record getRow(long key) {
		Record record = new Record();
		Cursor cur = db.query(TABLE_NAME, new String[]{"key", "id", "subject", "explanation", "process", "history"}, "key = ?", new String[]{String.valueOf(key)}, null, null, null); //
		if (cur.moveToFirst()) {
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

	/**
	 * 디버깅 메서드
	 * @param msg 메시지
	 */
	private void debug(String msg){
		if(isDebug){
			Log.e(DEBUG_TAG,"[WordDictionaryTable]"+msg);
		}
	}

	@SuppressWarnings("unused")
	public void setDebug(boolean debug) {
		isDebug = debug;
	}

	public class Record {
		private int key;
		public String id;
		String subject;
		String explanation;
		String process;
		String history;

		public String toString() {
			return "DataItem{id=[" + id + "], subject=[" + subject + "] explanation=[" + explanation + "], process=[" + process + "], history=[" + history + "]}";
		}

		@SuppressWarnings("unused")
		public int getKey() {
			return key;
		}

		void setKey(int key) {
			this.key = key;
		}

		@SuppressWarnings("unused")
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSubject() {
			return subject;
		}

		void setSubject(String subject) {
			this.subject = subject;
		}

		public String getExplanation() {
			return explanation;
		}

		void setExplanation(String explanation) {
			this.explanation = explanation;
		}

		public String getProcess() {
			return process;
		}

		void setProcess(String process) {
			this.process = process;
		}

		public String getHistory() {
			return history;
		}

		void setHistory(String history) {
			this.history = history;
		}
	}
}
