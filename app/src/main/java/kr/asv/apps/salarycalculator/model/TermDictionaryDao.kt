package kr.asv.apps.salarycalculator.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kr.asv.apps.salarycalculator.Services

/**
 * 용어 사전 데이터베이스에 연결하는 Dao 클래스
 */
class TermDictionaryDao(private val db: SQLiteDatabase) {
    private val isDebug = false
    /**
     * list 를 호출했을 때, 쿼리를 수행하고 Cursor 를 반환함
     */
    val list: Cursor
        get() {
            debug("getAll")
            return db.query(TABLE_NAME, arrayOf("id", "cid", "name", "description", "process", "history"), null, null, null, null, null)
        }

    init {
        debug("Constructor..")
    }

    /**
     * @param id Int
     * @return TermDictionary
     */
    fun getRow(id: Int): TermDictionary {
        debug("getRow")
        val record = TermDictionary()
        val cur = db.query(TABLE_NAME, arrayOf("id", "cid", "name", "description", "process", "history"), "id = ?", arrayOf(id.toString()), null, null, null) //
        if (cur.moveToFirst()) {
            record.id = cur.getInt(cur.getColumnIndex("id"))
            record.cid = cur.getString(cur.getColumnIndex("cid"))
            record.name = cur.getString(cur.getColumnIndex("name"))
            record.description = cur.getString(cur.getColumnIndex("description"))
            record.process = cur.getString(cur.getColumnIndex("process"))
            record.history = cur.getString(cur.getColumnIndex("history"))
        }
        cur.close()
        return record
    }

    /**
     * 콘텐츠 ID (cid) 를 기준으로 값을 조회해 옴.
     *
     * @param cid String (예시 : national_pension)
     * @return TermDictionary
     */
    fun getRowFromCID(cid: String): TermDictionary {
        debug("getRowFromCID")
        val record = TermDictionary()
        val cur = db.query(TABLE_NAME, arrayOf("id", "cid", "name", "description", "process", "history"), "cid = ?", arrayOf(cid.take(100)), null, null, null)
        if (cur.moveToFirst()) {
            record.id = cur.getInt(cur.getColumnIndex("id"))
            record.cid = cur.getString(cur.getColumnIndex("cid"))
            record.name = cur.getString(cur.getColumnIndex("name"))
            record.description = cur.getString(cur.getColumnIndex("description"))
            record.process = cur.getString(cur.getColumnIndex("process"))
            record.history = cur.getString(cur.getColumnIndex("history"))
        }
        cur.close()
        return record
    }

    /**
     * 디버깅 메서드
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun debug(msg: Any, msg2 : Any = "") {
        if (isDebug) {
            Services.debugLog("TermDictionaryDao", msg)
        }
    }

    companion object {
        private const val TABLE_NAME = "terminology_information"
    }
}