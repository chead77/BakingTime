package com.cheadtech.example.bakingtime.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface IngredientsDao {
    @Query("SELECT * FROM ingredients")
    List<Ingredients> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE recipe_id = :recipeId")
    List<Ingredients> getAllIngredientsForRecipe(Integer recipeId);

    @Insert
    void insertAll(ArrayList<Ingredients> ingredients);

    @Delete
    void delete(List<Ingredients> ingredient);
}
