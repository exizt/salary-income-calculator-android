package kr.asv.salarycalculator.app.databases

import android.content.Context
import kr.asv.salarycalculator.utils.DatabaseAssetCopyHandler

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


    init{
        initialize()
    }
}