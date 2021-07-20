package com.mysafe.lib_base.sqlite

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mysafe.lib_base.StandardConstants
import com.mysafe.lib_base.sqlite.dao.FaceDao
import com.mysafe.lib_base.sqlite.entity.FaceEntity
import java.io.File

@Database(entities = [FaceEntity::class], version = 1, exportSchema = false)
abstract class FaceDatabase : RoomDatabase() {
    abstract fun faceDao(): FaceDao

    companion object {
        @Volatile
        private var INSTANCE: FaceDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FaceDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            Log.i("TAG_FaceDatabase", "getDatabase: ${StandardConstants.appName}")
            try {
                INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                            context.applicationContext,
                            FaceDatabase::class.java,
//                        "face_DB"
//                        "/data/user/0/com.mysafe.msmealorder_public/databases/face_DB.db"
                            context.getExternalFilesDir("database").toString() + File.separator + "faceDB.db"
//                        "/storage/emulated/0/Android/data/${StandardConstants.appName}/files/database/faceDB.db"
                    )
                            .build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
            } catch (e: Exception) {

            }

            return INSTANCE!!
        }
    }
}