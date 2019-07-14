package com.cheadtech.example.bakingtime.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface IngredientsDao {
    @Query("SELECT * FROM ingredients ORDER BY recipe_id, row_id")
    List<IngredientsEntity> getAllIngredients();

    @Insert
    void insertAll(ArrayList<IngredientsEntity> ingredients);

    @Delete
    void delete(List<IngredientsEntity> ingredient);
}
