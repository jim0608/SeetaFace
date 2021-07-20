package com.mysafe.lib_base.sqlite.dao

import androidx.room.*
import com.mysafe.lib_base.sqlite.entity.FaceEntity

@Dao
interface FaceDao {
    /**
     * 获取库中所有已注册人脸
     *
     * @return 所有已注册人脸
     */
    @get:Query("SELECT * FROM face_table")
    val allFaces: MutableList<FaceEntity>?

    /**
     * 分页获取库中的人脸
     *
     * @param start 起始下标
     * @param size  单次获取的长度
     * @return 从下标为start开始的size个已注册人脸
     */
    @Query("SELECT * FROM face_table order by id desc limit :start,:size ")
    fun getFaces(start: Int, size: Int): List<FaceEntity>?

    /**
     * 获取已注册的人脸数
     *
     * @return
     */
    @get:Query("SELECT COUNT(1) from face_table")
    val faceCount: Int

    /**
     * 获取所有Id
     */
    @Query("SELECT id FROM face_table")
    fun getFaceIds():List<Int>

    /**
     * 更新已注册的人脸信息
     *
     * @param faceEntity 已注册的人脸信息
     * @return
     */
    @Update
    fun updateFaceEntity(faceEntity: FaceEntity?): Int

    /**
     * 删除人脸
     *
     * @param faceEntity 已注册的人脸信息
     * @return
     */
    @Delete
    fun deleteFace(faceEntity: FaceEntity?): Int

    /**
     * 批量删除指定ID人脸
     */
    @Query("DELETE from face_table WHERE id IN (:ids)")
    fun deleteById(ids: List<Int>): Int

    /**
     * 删除所有已注册的人脸
     *
     * @return
     */
    @Query("DELETE from face_table")
    fun deleteAll(): Int

    /**
     * 插入一个人脸入库
     *
     * @param faceEntity
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(faceEntity: FaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(faceEntities: MutableList<FaceEntity>): List<Long>

    @Transaction
    suspend fun updateFaceEntities(faceEntities: MutableList<FaceEntity>) {
        insertAll(faceEntities)
    }

}