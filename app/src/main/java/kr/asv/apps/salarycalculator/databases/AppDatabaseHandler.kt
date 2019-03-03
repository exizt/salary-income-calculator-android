package kr.asv.apps.salarycalculator.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * 실수령액 계산기 앱에서 기본적으로 사용하는 데이터베이스를 핸들링하는 클래스.
 * sqlite 데이터베이스 이고, readOnly 성격이다 (파일 자체는 writable 로 이용. 관리 편의를 위함)
 * 앱 내에서는 이 데이터베이스에 수정을 가하지 않도록 함.
 * <데이터베이스에 포함되는 내용>
 *     1) 용어 사전
 *     2) 소득세 간이세액표
 *     3) 세율 정보
 *
 * <데이터베이스의 동작 구현 및 업데이트 방식>
 *     최초 1회 및 앱 업데이트 시에는 Assets 에 있는 db 파일로 생성.
 *     디비 업데이트 기능을 구현하여, firebase storage 에 올라간 디비 파일로 업데이트 가능하게 구현.
 */
class AppDatabaseHandler (context: Context){
    private lateinit var mDatabase : SQLiteDatabase
    private val mDatabaseName = "salarytax_information.db"
    private lateinit var mDatabasePath :String
    private val isDebug = true
    private var currentDBVersion = 1
    private val mContext = context
    private val debugTag = "EXIZT-DEBUG"

    init{
        mDatabasePath = context.getDatabasePath(mDatabaseName).path

        load()
    }

    fun load(){
        if(!existsDatabaseLoaded(mContext)){
            debug("[open] 데이터베이스가 없음")
            // 데이터베이스가 없으므로, assets 에서 복사해와야 한다. // assets 마저도 없으면 exception
            // 최초 실행이므로, 이 정도만 하도록 함.

            // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
            if(existsAssetsDatabase(mContext,"db",mDatabaseName)) {
                copyDbFromAssets(mContext, "db/$mDatabaseName")
            }
        } else {
            debug("[open] 데이터베이스가 존재함")
            var currentDBVersion = getVersionFromDatabaseFile(File(mDatabasePath))
            debug("[open] 현재 db 버전 : $currentDBVersion")

            // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
            if(existsAssetsDatabase(mContext,"db",mDatabaseName)) {
                debug("[open] Assets 에서 데이터베이스 파일 복사")

                // 여기서 버전 비교 해야함.
                copyDbFromAssets(mContext, "db/$mDatabaseName")
            }

            // 앱을 업데이트하는 경우가 있다. 이 경우 assets 에 있는 db 가 최신일 수 있으므로 확인후 다운 받아야 한다.


            // firebase 의 파일을 받아서 업데이트 하는 로직...을 어디에 둘지 고민. 실행과 동시에 되려면 여기에 둔다.
        }

        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabasePath,  null)
    }

    fun getDb() : SQLiteDatabase{
        return mDatabase
    }
    /**
     * File 에서 version 정보를 가져오는 메서드
     * 연결을 해야하기 때문에 속도가 느릴 수도 있음...
     */
    private fun getVersionFromDatabaseFile(file: File): Int{
        // 파일의 존재 유무를 먼저 체크한다. 그런데 이미 file 개체로 넘어왔으니 있을 것 같은데..
        return if(file.exists())
        {
            var db:SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file,null)
            val version = db.version
            db.close()
            version
        } else {
            0
        }
    }

    /**
     * 현재 로드된 데이터베이스 가 존재하는지 여부 확인.
     * 특별한 경우가 없다면, 최초 1회 확인만 하게 될 듯.
     * 인수로 Context 를 넘겼는데, 혹시 모르니 불안해서 넣은 것임. 그대로 둘 것.
     * (데이터베이스명은 문자열로 들어갔고 변경여지가 없으나, Context 는 때에 따라서는 바뀌는 변수가 될 수 있음)
     */
    private fun existsDatabaseLoaded(context:Context): Boolean{
        var dbFile = context.getDatabasePath(mDatabaseName)
        debug("[existsDatabaseLoaded]"+dbFile.path)
        return dbFile.exists()
    }

    /**
     *
     */
    private fun existsAssetsDatabase(context:Context,path:String,dbName:String):Boolean {
        return context.assets.list(path).toList().contains(dbName)

    }

    /**
     * Assets 에 있는 파일을 DB 에 복사하는 메서드.
     */
    private fun copyDbFromAssets(context:Context, assetURI : String){
        FileOutputStream(mDatabasePath).use { out ->
            context.assets.open(assetURI).use {
                it.copyTo(out)
            }
        }
    }

    /**
     * 디버깅 메서드
     * @param msg 메시지
     */
    private fun debug(msg: String) {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            Log.e(debugTag, "[AppDatabaseHandler] $msg")
        }
    }
}