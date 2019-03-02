package kr.asv.apps.salarycalculator.databases

import android.content.Context

import kr.asv.androidutils.sqlite.SQLiteHandler

/**
 * WordInfoDBHandler
 * WordInfoDBHandler 테이블 을 처리하기 위한 클래스
 */
class WordInfoDBHandler
/**
 * Construction
 * @param context Context
 */
(context: Context) : SQLiteHandler() {
    private val dbName = "salarytax_information.db"
    private val dbVersion = 1
    private val isDebug = false

    init {
        debug("[Constructor] >> ")
        super.initialize(context, dbName, dbVersion, isDebug)
    }

    /**
     * onCreateDatabase
     */
    override fun onCreateDatabase() {
        debug("[onCreateDatabase] >> ")
        //copyDatabase();
    }

    /**
     * onUpgradeDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    override fun onUpgradeDatabase(oldVersion: Int, newVersion: Int) {
        debug("[onUpgradeDatabase] >>")
        //copyDatabase();
    }

    /**
     * onDowngradeDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    override fun onDowngradeDatabase(oldVersion: Int, newVersion: Int) {
        debug("[onDowngradeDatabase] >>")
        //copyDatabase();
    }

    override fun getDbVersion(): Int = dbVersion
}
