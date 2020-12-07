package kr.asv.salarycalculator.app.fragments

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.*
import kr.asv.salarycalculator.app.R
import kr.asv.salarycalculator.app.databases.AppDatabase

/**
 */
class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        displayAbout()
    }

    private fun displayAbout(){
        //tvDBVersion.text = Services.dbVersion.toString()
        //defaultSharedPreferences.getInt("DB_CURRENT_VERSION",0).toString()

        activity?.let {
            tvAppVersion.text = getAppVersion(it.applicationContext)
            tvDBVersion.text = getDbVersion(it.applicationContext)
        }
    }

    /**
     * 데이터베이스 파일의 버전 정보
     */
    private fun getDbVersion(application: Context): String {
        return AppDatabase.getInstance(application).openHelper.readableDatabase?.version.toString()
    }
    
    /**
     * 이 앱의 버전 정보
     */
    private fun getAppVersion(context: Context): String{
        var version = ""
        try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

}