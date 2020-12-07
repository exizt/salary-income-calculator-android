package kr.asv.salarycalculator.app.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface TermDao {
    @Query("SELECT * FROM terminology_information")
    fun getAll(): LiveData<List<Term>>

    @Query("SELECT * FROM terminology_information WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Term>

    @Query("SELECT * FROM terminology_information WHERE id = :id")
    fun findById(id: Int): LiveData<Term>

    @Query("SELECT * FROM terminology_information WHERE cid = :cid")
    fun findByCid(cid: String): LiveData<Term>
}