package kr.asv.salarycalculator.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import java.io.File
import java.io.IOException

/**
 *
 * <해당 데이터베이스의 동작 구현 및 업데이트 방식>
 *     최초 1회 및 앱 업데이트 시에는 Assets 에 있는 db 파일로 생성.
 *     디비 업데이트 기능을 구현하여, firebase storage 에 올라간 디비 파일로 업데이트 가능하게 구현.
 */
abstract class DatabaseAssetCopyHandler (context: Context){
    // context 레퍼런스
    protected val mContext = context
    // 앱에서 실행되는 DB 의 경로
    var databasePath :String = ""
        private set
    // 앱에서 실행되는 DB 의 파일명
    protected open val databaseName = "database.db"
    // 앱에서 실행되는 Database 의 버전의 설정값 이름
    protected open val prefKeyDbVersion = "DB_CURRENT_VERSION"
    // Assets 의 DB 파일의 파일명
    protected open val assetDbFilePath = "db/database.db"
    // Assets 의 DB 파일의 파일명
    private var assetDbFileName = "database.db"
    // Assets 의 DB 파일의 경로
    private var assetDbFileDirPath = "db"
    // Assets 의 DB 파일의 버전을 담고 있는 파일
    protected open val assetDbVersionFilePath = "db/database.db.version"
    protected open val debugTag = "[DEBUG]"
    protected open val debugSubTag = "DatabaseAssetCopyHandler"
    protected open val isDebug = false

    fun initialize(){
        // databasePath 지정
        databasePath = mContext.getDatabasePath(databaseName).path

        assetDbFileDirPath = assetDbFilePath.substringBeforeLast("/")
        assetDbFileName = assetDbFilePath.substringAfterLast("/")

        // copy 실행
        doCopy()

    }
    /**
     * 데이터베이스 파일을 생성하고 업데이트 하는 메서드
     * 디비 파일이 없으면, 디비 파일을 복사해 온다.
     * 디비 버전을 체크해서, Assets 에 있는 DB 가 최신 것이라면 복사해온다.
     */
    private fun doCopy(){
        // 설정 관리자
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)

        // Local 데이터베이스가 있는지 확인하고, 분기를 나눔.
        if(!existsAppDatabase(mContext)){
            // 보통 첫 설치 때의 동작. 속도가 느릴 수 있으므로, 최대한 간단히 동작하도록 한다.

            debug("[doCopy] (분기) 로컬 DB가 존재하지 않음. 복사 실행.")
            // 데이터베이스가 없으므로, assets 에서 복사해와야 한다. // assets 마저도 없으면 exception
            // 최초 실행이므로, 이 정도만 하도록 함.

            try {
                // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
                if (existsAssetDatabase(mContext)) {
                    // 한번은 생성을 해야할 듯 하다...
                    // 오래된 버전의 경우 databases 디렉토리를 생성 안 하는 버그가 있다.
                    File(databasePath).also{
                        file -> file.parentFile?.mkdirs()
                    }.createNewFile()

                    debug("[doCopy] Assets 에서 데이터베이스 파일 복사 실행")
                    copyDatabaseFromAssets(mContext)
                    setPreferenceDbVersion(prefs, getAssetDbVersion(mContext))
                    debug("[doCopy] setAppDbVersionPreference")
                }
            } catch (e: Exception){
                debug("[doCopy] Assets 에서 복사 실패 ",e)
            }

        } else {
            // 앱을 실행하거나, 업데이트 했을 때 등의 동작.
            debug("[doCopy] (분기) 로컬 DB가 존재함")
            val localDbVersion = getPreferenceDbVersion(prefs)
            val assetDbVersion = getAssetDbVersion(mContext)

            debug("[doCopy] 기존 로컬 DB 버전 : ",localDbVersion)
            debug("[doCopy] Assets 에 위치한 DB 버전 : ",assetDbVersion)

            // assets 의 버전이 높을 경우, assets 의 파일을 다운로드 함.
            if(localDbVersion < assetDbVersion){
                try {
                    // assets 디비 파일이 있는지 체크하고, 있을 시에 복사함
                    if(existsAssetDatabase(mContext)) {
                        debug("[doCopy] Assets 에서 데이터베이스 파일 복사 실행")
                        copyDatabaseFromAssets(mContext)
                        setPreferenceDbVersion(prefs,assetDbVersion)
                    }
                } catch (e: Exception) {
                    debug("[doCopy] Assets 에서 복사 실패  ",e)
                }
            }
        }
    }

    /**
     * Preferences 에서 App DB 버전 값을 가져옴.
     * PreferenceManager 를 여러번 이용할 경우에는
     * getAppDbVersionPreference(prefs : SharedPreferences) 를 이용하기를 권장함.
     */
    @Suppress("unused")
    fun getPreferenceDbVersion() : Int{
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        return getPreferenceDbVersion(prefs)
    }

    /**
     * Preferences 에서 App DB 버전 값을 가져옴
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getPreferenceDbVersion(prefs : SharedPreferences) : Int{
        return prefs.getInt(prefKeyDbVersion,0)
    }

    /**
     * 버전 정보값을 Preferences 에 저장 하는 메서드.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun setPreferenceDbVersion(prefs : SharedPreferences, version:Int){
        val editor = prefs.edit()
        editor.putInt(prefKeyDbVersion,version)
        editor.apply()
    }

    /**
     * Assets 에 있는 Version 이 담긴 text 파일에서 버전 정보를 읽어온다.
     * 에러가 있을 경우 그저 0 을 반환한다.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getAssetDbVersion(context:Context):Int{
        return try {
            //debug("[getAssetDbVersion] path : ",assetDbVersionFilePath)
            Integer.parseInt(AssetHandler.readTextFile(context, assetDbVersionFilePath))
        } catch (e: Exception) {
            debug("[getAssetDbVersion] exception", e)
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
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun existsAppDatabase(context:Context): Boolean{
        val dbFile = context.getDatabasePath(databaseName)
        //debug("[existsDatabaseLoaded]",dbFile.path)
        return dbFile.exists()
    }

    /**
     * Assets 에 데이터베이스 파일이 존재하는지 유무.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun existsAssetDatabase(context:Context):Boolean {
        return AssetHandler.existsFile(context, assetDbFileDirPath, assetDbFileName)
    }

    /**
     * Assets 에 있는 DB 파일을 local DB 에 복사.
     */
    @Throws(IOException::class)
    protected fun copyDatabaseFromAssets(context:Context){
        AssetHandler.copyFile(context, assetDbFilePath, databasePath)
    }

    /**
     * 디버깅 메서드
     * 변수가 두개 넘어올 경우의 처리 추가
     * @param msg 메시지
     */
    @Suppress("unused")
    protected fun debug(msg: Any, msg2 : Any = "") {
        @Suppress("ConstantConditionIf")
        if (isDebug) {
            Log.d(debugTag, "($debugSubTag) : $msg $msg2")
        }
    }
}