package com.cheadtech.example.bakingtime.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM recipes")
    List<Recipes> getAllRecipes();

    @Query("SELECT * FROM recipes")
    LiveData<List<Recipes>> getAllRecipesLiveData();

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    Recipes getRecipe(Integer recipeId);

    @Insert
    void insertAll(List<Recipes> recipes);

    @Delete
    void delete(List<Recipes> recipes);
}
