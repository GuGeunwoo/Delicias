package com.example.delicias.data.repository.datasource

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objs: List<T>)

    @Delete
    suspend fun delete(obj: T)
}