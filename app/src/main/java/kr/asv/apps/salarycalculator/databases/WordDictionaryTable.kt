package kr.asv.apps.salarycalculator.databases

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
 * 단어 사전 테이블 클래스
 * Created by EXIZT on 2016-04-30.
 */
class WordDictionaryTable(private val db: SQLiteDatabase) {
	private var isDebug = false
	private val debugTag = "EXIZT-DEBUG"
	private val tableName = "word_dictionary"

	val list: Cursor
		get() {
			debug("query")
			return db.query(tableName, arrayOf("key", "id", "subject", "explanation", "process", "history"), null, null, null, null, null)
		}

	init {
		debug("Constructor..")
	}

	/**
	 * @param key long
	 * @return Record
	 */
	fun getRow(key: Int): Record {
		val record = Record()
		val cur = db.query(tableName, arrayOf("key", "id", "subject", "explanation", "process", "history"), "key = ?", arrayOf(key.toString()), null, null, null) //
		if (cur.moveToFirst()) {
			record.key = cur.getInt(cur.getColumnIndex("key"))
			record.id = cur.getString(cur.getColumnIndex("id"))
			record.subject = cur.getString(cur.getColumnIndex("subject"))
			record.explanation = cur.getString(cur.getColumnIndex("explanation"))
			record.process = cur.getString(cur.getColumnIndex("process"))
			record.history = cur.getString(cur.getColumnIndex("history"))
		}
		cur.close()
		return record
	}

	/**
	 * @param id string
	 * @return Record
	 */
	fun getRowFromId(id: String): Record {
		val record = Record()
		val cur = db.query(tableName, arrayOf("key", "id", "subject", "explanation", "process", "history"), "id = ?", arrayOf(id), null, null, null) //
		if (cur.moveToFirst()) {
			record.key = cur.getInt(cur.getColumnIndex("key"))
			record.id = cur.getString(cur.getColumnIndex("id"))
			record.subject = cur.getString(cur.getColumnIndex("subject"))
			record.explanation = cur.getString(cur.getColumnIndex("explanation"))
			record.process = cur.getString(cur.getColumnIndex("process"))
			record.history = cur.getString(cur.getColumnIndex("history"))
		}
		cur.close()
		return record
	}

	/**
	 * 디버깅 메서드
	 * @param msg 메시지
	 */
	private fun debug(msg: String) {
		if (isDebug) {
			Log.e(debugTag, "[WordDictionaryTable]$msg")
		}
	}

	@Suppress("unused")
	fun setDebug(debug: Boolean) {
		isDebug = debug
	}

	companion object {
		data class Record(var key:Int=0, var id:String?="", var subject:String?="", var explanation:String?="", var process:String?="", var history:String?="")
	}
}
