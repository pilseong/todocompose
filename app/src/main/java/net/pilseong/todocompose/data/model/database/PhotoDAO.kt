package net.pilseong.todocompose.data.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import net.pilseong.todocompose.data.model.Photo

@Dao
abstract class PhotoDAO {

    @Query("SELECT * FROM photo_table WHERE id = :id")
    abstract suspend fun getPhoto(id: Int): Photo

    @Query("SELECT * FROM photo_table where memoId = :memoId")
    abstract suspend fun getPhotosByMemoId(memoId: Int): List<Photo>

    @Insert
    abstract suspend fun addPhoto(photo: Photo)

    @Transaction
    open suspend fun addPhotos(photos: List<Photo>) {
        photos.forEach { photo ->
            addPhoto(photo)
        }
    }
}