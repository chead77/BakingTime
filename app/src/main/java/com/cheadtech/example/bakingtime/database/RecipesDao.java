package com.cheadtech.example.bakingtime.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import java.util.List;

@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@Dao
public interface RecipesDao {
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY id")
    List<RecipeEntity> getAllRecipes();

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY id")
    List<RecipeModel> getAllRecipesWithLists();

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY id")
    LiveData<List<RecipeModel>> getAllRecipesWithListsLiveData();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    RecipeModel getRecipeWithLists(Integer recipeId);

    @Insert
    void insertAll(List<RecipeEntity> recipes);

    @Delete
    void delete(List<RecipeEntity> recipes);
}
