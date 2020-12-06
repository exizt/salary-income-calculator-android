package kr.asv.salarycalculator.app.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kr.asv.salarycalculator.app.model.Term
import kr.asv.salarycalculator.app.model.TermDao

@Database(entities = [Term::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun termDao(): TermDao

    // singleton 구현
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            return INSTANCE ?: synchronized(AppDatabase::class) {
                INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "income_salary.db")
                        .createFromAsset("db/salarytax_information.db")
                        .fallbackToDestructiveMigration()
                        .build().also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}