package kr.asv.salarycalculator.app.model

import android.app.Application
import androidx.lifecycle.LiveData
import kr.asv.salarycalculator.app.databases.AppDatabase

class TermRepository (application: Application){
    private val termDao: TermDao by lazy {
        val db = AppDatabase.getInstance(application)
        db.termDao()
    }
    private val terms: LiveData<List<Term>> by lazy {
        termDao.getAll()
    }

    fun getAll(): LiveData<List<Term>> {
        return terms
    }

    fun findById(id: Int): LiveData<Term> {
        return termDao.findById(id)
    }

    fun findByCid(id: String): LiveData<Term> {
        return termDao.findByCid(id)
    }
}