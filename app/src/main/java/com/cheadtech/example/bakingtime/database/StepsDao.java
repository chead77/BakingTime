package com.cheadtech.example.bakingtime.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StepsDao {
    @Query("SELECT * FROM steps WHERE recipe_id = :recipeId ORDER BY step_id")
    List<StepsEntity> getAllStepsForRecipe(Integer recipeId);

    @Query("SELECT * FROM steps ORDER BY recipe_id, step_id")
    List<StepsEntity> getAllSteps();

    @Insert
    void insertAll(ArrayList<StepsEntity> steps);

    @Delete
    void delete(List<StepsEntity> steps);
}
