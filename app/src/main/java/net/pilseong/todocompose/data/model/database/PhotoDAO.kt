package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import net.pilseong.todocompose.data.model.Photo

@Dao
abstract class PhotoDAO {

    @Query("SELECT * FROM photo_table WHERE id = :id")
    abstract suspend fun getPhoto(id: Long): Photo

    @Query("SELECT * FROM photo_table where memoId = :memoId")
    abstract suspend fun getPhotosByMemoId(memoId: Long): List<Photo>

    @Query("SELECT id FROM photo_table where memoId = :memoId")
    abstract suspend fun getPhotoIdsByMemoId(memoId: Long): List<Long>

    @Query("DELETE FROM photo_table WHERE id = :id")
    abstract suspend fun deletePhotoById(id: Long)

    @Insert
    abstract suspend fun addPhoto(photo: Photo)

    @Transaction
    open suspend fun addPhotos(photos: List<Photo>) {
        photos.forEach { photo ->
            addPhoto(photo)
        }
    }
}