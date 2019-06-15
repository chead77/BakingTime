package com.cheadtech.example.bakingtime.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Recipes.class, Ingredients.class, Steps.class}, version = 1, exportSchema = false)
public abstract class BakingTimeDB extends RoomDatabase {
    public abstract RecipesDao recipesDao();
    public abstract IngredientsDao ingredientsDao();
    public abstract StepsDao stepsDao();
}
