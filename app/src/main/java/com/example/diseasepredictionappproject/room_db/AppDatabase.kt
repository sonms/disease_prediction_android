package com.example.diseasepredictionappproject.room_db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.diseasepredictionappproject.room_db.medicine.MedicineDao
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity

@RequiresApi(Build.VERSION_CODES.O)
@Database(entities = [PredictionEntity::class, MedicineEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPredictionDao(): PredictionDao
    abstract fun getMedicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "disease-prediction-database"
            )
                //.fallbackToDestructiveMigration()
                //.addMigrations(migration_1_2) // 마이그레이션 추가
                .build()

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }


        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // isBookMark 컬럼 추가: BOOLEAN 타입으로 기본값 0(즉, false)
                db.execSQL(
                    "ALTER TABLE MedicineTable ADD COLUMN isBookMark INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}