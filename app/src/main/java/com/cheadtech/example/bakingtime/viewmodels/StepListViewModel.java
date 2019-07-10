package com.cheadtech.example.bakingtime.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.IngredientsEntity;
import com.cheadtech.example.bakingtime.database.RecipeEntity;
import com.cheadtech.example.bakingtime.database.StepsEntity;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;

import java.util.ArrayList;

public class StepListViewModel extends ViewModel {
    private final String tag = getClass().toString();
    private BakingTimeDB db;
    private int recipeId;

    public MutableLiveData<Recipe> recipeLiveData = new MutableLiveData<>();

    public interface StepListViewModelCallback {
        void onDBError();
    }
    private StepListViewModelCallback callback;

    public void init(@NonNull BakingTimeDB db, int recipeId, StepListViewModelCallback callback) {
        this.db = db;
        this.recipeId = recipeId;
        this.callback = callback;
        loadRecipeComponents();
    }

    private void loadRecipeComponents() {
        new Thread(() -> {
            try {
                RecipeEntity dbRecipe = db.recipesDao().getRecipe(recipeId);
                ArrayList<IngredientsEntity> dbIngredients = new ArrayList<>(db.ingredientsDao().getAllIngredientsForRecipe(recipeId));
                ArrayList<StepsEntity> dbSteps = new ArrayList<>(db.stepsDao().getAllStepsForRecipe(recipeId));

                Recipe recipe = new Recipe();
                recipe.id = dbRecipe.id;
                recipe.name = dbRecipe.name;
                recipe.servings = dbRecipe.servings;
                recipe.image = dbRecipe.image;
                recipe.ingredients = new ArrayList<>();
                for (IngredientsEntity dbIngredient : dbIngredients) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.quantity = dbIngredient.quantity;
                    ingredient.measure = dbIngredient.measure;
                    ingredient.ingredient = dbIngredient.ingredient;
                    recipe.ingredients.add(ingredient);
                }
                recipe.steps = new ArrayList<>();
                for (StepsEntity dbStep : dbSteps) {
                    Step step = new Step();
                    step.id = dbStep.stepId;
                    step.shortDescription = dbStep.shortDescription;
                    step.description = dbStep.description;
                    step.thumbnailURL = dbStep.thumbnailUrl;
                    step.videoURL = dbStep.videoUrl;
                    recipe.steps.add(step);
                }
                recipeLiveData.postValue(recipe);
            } catch (Exception e) {
                Log.e(tag, "DB error: " + e.getMessage());
                callback.onDBError();
            }
        }).start();
    }
}
