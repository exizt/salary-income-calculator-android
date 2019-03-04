package kr.asv.apps.salarycalculator.databases

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 실수령액 계산기 앱에서 기본적으로 사용하는 데이터베이스를 핸들링하는 클래스.
 * sqlite 데이터베이스 이고, readOnly 성격이다 (파일 자체는 writable 로 이용. 관리 편의를 위함)
 * 앱 내에서는 이 데이터베이스에 수정을 가하지 않도록 함.
 * (데이터 저장 및 변경을 위한 디비는 별도로 구성할 것)
 *
 * <해당 데이터베이스에 포함되는 내용>
 *     1) 용어 사전
 *     2) 소득세 간이세액표
 *     3) 세율 정보
 *
 * <해당 데이터베이스의 동작 구현 및 업데이트 방식>
 *     최초 1회 및 앱 업데이트 시에는 Assets 에 있는 db 파일로 생성.
 *     디비 업데이트 기능을 구현하여, firebase storage 에 올라간 디비 파일로 업데이트 가능하게 구현.
 */
class AppDatabaseHandler (context: Context){
    // context 레퍼런스
    private val mContext = context
    // database 객체
    private lateinit var mDatabase : SQLiteDatabase
    // 앱에서 실행되는 Database File 의 이름
    private val mDatabaseName = "salarytax_information.db"
    // 앱에서 실행되는 Database File 의 전체 경로
    private var mDatabasePath :String
    // 앱에서 실행되는 Database 의 버전의 설정값 이름
    private val localDbVersionPreferenceCode = "DB_CURRENT_VERSION"
    // assets 의 DB 파일의 경로
    private val assetDbFilePath = "db"
    // assets 의 DB 파일의 파일명
    private val assetDbFileName = "salarytax_information.db"
    // assets 의 DB 파일의 버전을 담고 있는 파일
    private val assetDbVersionFileName = "salarytax_information.db.version"
    // firebaseStorage 에 위치한 파일위치
    private val firebaseStorageDBFilePath = "apps/income-salary-calculator/income-salary-calculator-db.db"

    private val isDebug = true
    private val debugTag = "EXIZT-DEBUG"


    init{
        mDatabasePath = context.getDatabasePath(mDatabaseName).path

        initialize()
    }

    /**
     * 데이터베이스 파일을 생성하고 업데이트 하는 메서드
     * 디비 파일이 없으면, 디비 파일을 복사해 온다.
     * 디비 버전을 체크해서, Assets 에 있는 DB 가 최신 것이라면 복사해온다.
     */
    private fun initialize(){
        // 설정 관리자
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)

        // Local 데이터베이스가 있는지 확인하고, 분기를 나눔.
        if(!existsDatabaseLoaded(mContext)){
            // 보통 첫 설치 때의 동작. 속도가 느릴 수 있으므로, 최대한 간단히 동작하도록 한다.

            debug("[open] 데이터베이스가 없음")
            // 데이터베이스가 없으므로, assets 에서 복사해와야 한다. // assets 마저도 없으면 exception
            // 최초 실행이므로, 이 정도만 하도록 함.

            // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
            if(existsAssetsDatabase(mContext)) {
                copyDatabaseFromAssets(mContext)
            }

        } else {
            // 앱을 실행하거나, 업데이트 했을 때 등의 동작.
            debug("[open] 데이터베이스가 존재함")
            val localDbVersion = getLocalDbVersionFromPreferences(prefs)
            val assetDbVersion = getAssetDbVersion(mContext)

            debug("[open] 실행 초기 로컬 DB 버전 : $localDbVersion")
            debug("[open] Assets 에 위치한 DB 버전 : $assetDbVersion")

            // assets 의 버전이 높을 경우, assets 의 파일을 다운로드 함.
            if(localDbVersion < assetDbVersion){
                try {
                    // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
                    if(existsAssetsDatabase(mContext)) {
                        debug("[open] Assets 에서 데이터베이스 파일 복사 실행")
                        copyDatabaseFromAssets(mContext)
                    }
                } catch (e: Exception) {
                    debug("[open] copyDatabaseFromAssets 에러 $e")
                }
            }
        }

        // 데이터베이스 를 Open
        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabasePath,  null)
        setLocalDbVersionToPreferences(prefs)
        debug("[open] DB 버전 : ${mDatabase.version}")
    }

    /**
     * 데이터베이스 경로를 리턴.
     */
    fun getDatabasePath() : String{
        return mDatabasePath
    }

    /**
     * Preferences 에서 LocalDB 버전 값을 가져옴.
     * PreferenceManager 를 여러번 이용할 경우에는
     * getLocalDbVersionFromPreferences(prefs : SharedPreferences) 를 이용하기를 권장함.
     */
    @Suppress("unused")
    private fun getLocalDbVersionFromPreferences() : Int{
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        return getLocalDbVersionFromPreferences(prefs)
    }

    /**
     * Preferences 에서 LocalDB 버전 값을 가져옴
     */
    @Suppress("unused")
    private fun getLocalDbVersionFromPreferences(prefs : SharedPreferences) : Int{
        return prefs.getInt(localDbVersionPreferenceCode,0)
    }

    /**
     * 버전 정보값을 Preferences 에 저장
     * <주의> LocalDB 가 오픈되어 있어야 함.
     */
    private fun setLocalDbVersionToPreferences(prefs : SharedPreferences){
        val editor = prefs.edit()
        editor.putInt(localDbVersionPreferenceCode,mDatabase.version)
        editor.apply()
    }

    /**
     * Assets 에 있는 Version 이 담긴 text 파일에서 버전 정보를 읽어온다.
     * 에러가 있을 경우 그저 0 을 반환한다.
     */
    private fun getAssetDbVersion(context:Context):Int{
        return try {
            Integer.parseInt(readTextFromAssets(context,"$assetDbFilePath/$assetDbVersionFileName"))
        } catch (e: Exception) {
            0
        }
    }

    /**
     * File 에서 version 정보를 가져오는 메서드
     * 연결을 해야하기 때문에 속도가 느릴 수도 있음...
     */
    @Suppress("unused")
    private fun getVersionFromDatabaseFile(file: File): Int{
        // 파일의 존재 유무를 먼저 체크한다. 그런데 이미 file 개체로 넘어왔으니 있을 것 같은데..
        return if(file.exists())
        {
            val db:SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file,null)
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
     * 데이터베이스를 open 해서 try 로 확인하는 방법이 좀 더 유용하지만,
     * 속도면에서 file 의 존재만 확인 하는 것이 나을 것으로 보임.
     */
    private fun existsDatabaseLoaded(context:Context): Boolean{
        val dbFile = context.getDatabasePath(mDatabaseName)
        //debug("[existsDatabaseLoaded]"+dbFile.path)
        return dbFile.exists()
    }

    /**
     * Assets 에 데이터베이스 파일이 존재하는지 유무.
     */
    private fun existsAssetsDatabase(context:Context):Boolean {
        return existsAssetFile(context,assetDbFilePath,assetDbFileName)
    }

    /**
     * Assets 에 있는 DB 파일을 local DB 에 복사.
     */
    @Throws(IOException::class)
    private fun copyDatabaseFromAssets(context:Context){
        copyFromAssets(context, "$assetDbFilePath/$assetDbFileName", mDatabasePath)
    }

    /**
     * Assets 에 파일이 있는지 확인하는 메서드.
     * assets 에서 한 단계 밑의 폴더 내의 파일을 확인할 수 있다.
     * 두 단계 이상으로는 구현 안 함. 오히려 비효율적일 수 있어서.
     */
    private fun existsAssetFile(context:Context,path:String,fileName:String):Boolean {
        return context.assets.list(path).toList().contains(fileName)
    }

    /**
     *
     */
    private fun readTextFromAssets(context:Context, assetURI:String) : String{
        return context.assets.open(assetURI).bufferedReader().use {
            it.readText()
        }
    }
    /**
     * Assets 에서 파일을 복사하는 메서드.
     * 일반적으로 사용 가능함.
     */
    @Throws(IOException::class)
    private fun copyFromAssets(context:Context, assetURI : String, toPath:String){
        FileOutputStream(toPath).use { out ->
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

    /**
     * Firebase 를 통해서 db 파일을 다운로드 함.
     * cache 에 저장함.
     */
    @Suppress("unused")
    fun copyStorageDBFile(){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val islandRef = storageRef.child(firebaseStorageDBFilePath)
        val cacheFileName = "testDB.db"
        val localFile = File.createTempFile(cacheFileName, ".db")

        // firebase cloud 에서 파일을 다운로드 함
        islandRef.getFile(localFile).addOnSuccessListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)

            // 현재 LocalDB 의 버전
            val localDbVersion = getLocalDbVersionFromPreferences(prefs)

            // 받은 파일의 버전
            val version = getVersionFromDatabaseFile(localFile)

            // 버전과 기존 디비의 버전을 비교하고, 새로 받은 파일의 버전이 높다면, 복사를 시행함.
            if(localDbVersion < version){
                // 복사하기 전에, 기존의 디비는 close 시켜야 함.
                mDatabase.close()

                // 복사 시행
                localFile.copyTo(File(mContext.getDatabasePath(mDatabaseName).path),true)

                // 복사 완료된 DB 로 재배치.
                mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabasePath,  null)
                setLocalDbVersionToPreferences(prefs)

                // cache 에 있는 데이터베이스 파일을 삭제해야 할 듯...
                localFile.delete()
            }
        }.addOnFailureListener {
            // Handle any errors
        }

    }
}