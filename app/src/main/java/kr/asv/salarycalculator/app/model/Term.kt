package kr.asv.salarycalculator.app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terminology_information")
data class Term(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "cid") val cid: String,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "description") val description: String?,
        @ColumnInfo(name = "process") val process: String?,
        @ColumnInfo(name = "history") val history: String?,
        @ColumnInfo(name = "created_at") val createdAt: String?,
        @ColumnInfo(name = "updated_at") val updatedAt: String?
)
