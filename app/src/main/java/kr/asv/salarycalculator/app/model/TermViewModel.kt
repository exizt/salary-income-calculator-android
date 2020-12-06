package kr.asv.salarycalculator.app.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class TermViewModel (application: Application): AndroidViewModel(application){
    private val repository: TermRepository by lazy {
        TermRepository(application)
    }

    private val terms: LiveData<List<Term>> by lazy {
        repository.getAll()
    }

    fun getAll(): LiveData<List<Term>> {
        Log.d("[EXIZT-SCalculator]", "TermViewModel.getAll")
        return terms
    }

    fun findById(id: Int): LiveData<Term> {
        return repository.findById(id)
    }

    fun findByCid(id: String): LiveData<Term> {
        return repository.findByCid(id)
    }
}