package com.cheadtech.example.bakingtime.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM recipes ORDER BY id")
    List<RecipeEntity> getAllRecipesOrderedById();

    @Query("SELECT * FROM recipes ORDER BY id")
    LiveData<List<RecipeEntity>> getAllRecipesOrderedByIdLiveData();

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    RecipeEntity getRecipe(Integer recipeId);

    @Insert
    void insertAll(List<RecipeEntity> recipes);

    @Delete
    void delete(List<RecipeEntity> recipes);
}
