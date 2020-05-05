package com.example.mymovie.Database;

import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mymovie.Modal.MoviesData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
@Dao
public interface TaskDao {

    @Query("SELECT * FROM moviesdata")
    LiveData<List<MoviesData>> loadAllMovies();

    @Insert
    void insertTask(MoviesData moviesData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(MoviesData moviesData);
    @Delete
    void deleteTask(MoviesData moviesData);
    @Query("SELECT * FROM moviesdata WHERE id=:id")
    MoviesData selectSpecialId(int id);
}
