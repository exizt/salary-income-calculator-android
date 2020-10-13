package kr.asv.apps.salarycalculator.fragments

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import kr.asv.apps.salarycalculator.R
import org.jetbrains.anko.defaultSharedPreferences

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
        }
    }

    /**
     * 이 앱의 버전 조회
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