package kr.asv.androidutils

import android.content.Context
import java.io.FileOutputStream
import java.io.IOException

/**
 * AssetHandler
 *
 * Assets 에서 간단한 파일 복사, 파일 read 등을 구현한 클래스이다.
 *
 * Author : EXIZT
 * Release : 2020-10-10
 * Ver : 1.0.0
 */
class AssetHandler {
    companion object {

        /**
         * 텍스트파일에서 내용을 String 으로 읽는 메서드.
         */
        fun readTextFile(context:Context, assetURI:String) : String{
            return context.assets.open(assetURI).bufferedReader().use {
                it.readText()
            }
        }

        /**
         * Assets 에서 파일을 복사하는 메서드.
         * 일반적으로 사용 가능함.
         */
        @Throws(IOException::class)
        fun copyFile(context:Context, assetURI : String, toPath:String){
            FileOutputStream(toPath).use { out ->
                context.assets.open(assetURI).use {
                    it.copyTo(out)
                }
            }
        }

        /**
         * Assets 에 파일이 있는지 확인하는 메서드.
         * assets 에서 한 단계 밑의 폴더 내의 파일을 확인할 수 있다.
         * 두 단계 이상으로는 구현 안 함. 오히려 비효율적일 수 있어서.
         */
        fun existsFile(context: Context, dirPath:String, fileName:String):Boolean {
            if(context.assets.list(dirPath)==null) return false
            return context.assets.list(dirPath)!!.toList().contains(fileName)
        }
    }
}