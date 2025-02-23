package com.example.diseasepredictionappproject.room_db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diseasepredictionappproject.room_db.alarm.AlarmDao
import com.example.diseasepredictionappproject.room_db.alarm.AlarmEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineDao
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity

@RequiresApi(Build.VERSION_CODES.O)
@Database(entities = [PredictionEntity::class, MedicineEntity::class, AlarmEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPredictionDao(): PredictionDao
    abstract fun getMedicineDao(): MedicineDao

    abstract fun getAlarmDao() : AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "disease-prediction-database"
            )
                .fallbackToDestructiveMigration()
                //.addMigrations(migration_2_3) // 마이그레이션 추가
                .build()

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }


        /*val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // PredictionTable에 alarmId 컬럼 추가: INTEGER 타입으로 기본값 NULL
                db.execSQL("ALTER TABLE PredictionTable ADD COLUMN alarmId INTEGER")
                db.execSQL("ALTER TABLE MedicineTable ADD COLUMN alarmId INTEGER")
            }
        }*/
    }
}