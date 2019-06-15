package com.cheadtech.example.bakingtime.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StepsDao {
    @Query("SELECT * FROM steps WHERE recipe_id = :recipeId")
    List<Steps> getAllStepsForRecipe(Integer recipeId);

    @Query("SELECT * FROM steps")
    List<Steps> getAllSteps();

    @Insert
    void insertAll(Steps... steps);

    @Delete
    void delete(List<Steps> steps);
}
