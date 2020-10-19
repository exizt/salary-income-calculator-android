package kr.asv.apps.salarycalculator.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.preference.PreferenceManager
import com.google.firebase.storage.FirebaseStorage
import kr.asv.androidutils.DatabaseAssetCopyHandler
import java.io.File

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
class AppDatabaseHandler (context: Context) : DatabaseAssetCopyHandler(context) {
    override val isDebug = true

    // 앱에서 실행되는 Database File 의 이름
    override val databaseName = "salarytax_information.db"
    // 앱에서 실행되는 Database 의 버전의 설정값 이름
    override val prefKeyDbVersion = "DB_CURRENT_VERSION"
    // assets 의 DB 파일의 파일명
    override val assetDbFilePath = "db/salarytax_information.db"
    // assets 의 DB 파일의 버전을 담고 있는 파일
    override val assetDbVersionFilePath = "db/salarytax_information.db.version"
    override val debugTag = "[EXIZT-DEBUG]"
    override val debugSubTag = "AppDatabaseHandler"

    // firebaseStorage 에 위치한 파일위치
    private val firebaseStorageDBFilePath = "apps/income-salary-calculator/income-salary-calculator-db.db"


    init{
        initialize()
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
     * Firebase 를 통해서 db 파일을 다운로드 함.
     * cache 에 저장함.
     */
    @Suppress("unused")
    fun copyFirebaseStorageDbFile(){
        copyFirebaseStorageDbFile(mContext,firebaseStorageDBFilePath,databaseName,"testDb.db")
    }

    /**
     * Firebase 를 통해서 db 파일을 다운르도 하고, 복사하는 로직.
     */
    @Suppress("SameParameterValue")
    private fun copyFirebaseStorageDbFile(context: Context, firebasePath : String, databaseName : String, cacheFileName : String = "tempDB.db")
    {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val islandRef = storageRef.child(firebasePath)
        val localFile = File.createTempFile(cacheFileName, ".db")

        // firebase cloud 에서 파일을 다운로드 함
        islandRef.getFile(localFile).addOnSuccessListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            // 현재 LocalDB 의 버전
            val localDbVersion = getPreferenceDbVersion(prefs)

            // 받은 파일의 버전
            val version = getVersionFromDatabaseFile(localFile)

            // 버전과 기존 디비의 버전을 비교하고, 새로 받은 파일의 버전이 높다면, 복사를 시행함.
            if(localDbVersion < version){
                debug("[copyFirebaseStorageDbFile] 복사 시도")

                // 복사하기 전에, 기존의 디비는 close 시켜야 함.
                //mDatabase.close()

                // 복사 시행
                try{
                    localFile.copyTo(File(context.getDatabasePath(databaseName).path),true)
                } catch (e: Exception) {
                    debug("[copyFirebaseStorageDbFile] localFile copyTo Error")
                }

                // 복사 완료된 DB 로 재배치.
                //mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabasePath,  null)
                setPreferenceDbVersion(prefs,version)

                // cache 에 있는 데이터베이스 파일을 삭제해야 할 듯...

                // 완료
                debug("[copyFirebaseStorageDbFile] 복사 완료")

                // 여기서 버전 히스토리를 남겨도 좋을 듯 한데... 어디다 남길지 모르겠음...

                // 세율 정보값에 변경된 값을 반영함.
                // Services.setDefaultInsuranceRates(context)
            } else {
                debug("[copyFirebaseStorageDbFile] 새로운 버전이 아니므로, 복사 안 함")
            }
            localFile.delete()
        }.addOnFailureListener {
            // Handle any errors
            // 에러시 어떤 동작을 취할지도 고민... 에러시 에러를 개발자에게 알려주는 것이 좋을 듯.

        }
    }
}