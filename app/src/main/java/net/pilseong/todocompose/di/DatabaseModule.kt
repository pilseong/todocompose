package net.pilseong.todocompose.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.pilseong.todocompose.data.model.database.MemoDAO
import net.pilseong.todocompose.data.model.database.MemoDatabase
import net.pilseong.todocompose.data.model.database.NotebookDAO
import net.pilseong.todocompose.util.Constants.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MemoDatabase = Room.databaseBuilder(
        context = context,
        MemoDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideMemoDAO(database: MemoDatabase): MemoDAO {
        return database.getMemoDAO()
    }

    @Singleton
    @Provides
    fun provideNotebookDAO(database: MemoDatabase): NotebookDAO {
        return database.getNotebookDAO()
    }
}