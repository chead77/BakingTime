package com.cheadtech.example.bakingtime.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.IngredientsEntity;
import com.cheadtech.example.bakingtime.database.RecipeEntity;
import com.cheadtech.example.bakingtime.database.StepsEntity;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;

import java.util.ArrayList;
import java.util.List;

public class BakingTimeUtil {
    @Nullable
    public static ArrayList<Recipe> getRecipeArrayListFromDB(@NonNull BakingTimeDB db) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        List<RecipeEntity> allRecipes = db.recipesDao().getAllRecipes();
        if (allRecipes == null || allRecipes.isEmpty())
            return null;

        List<IngredientsEntity> allIngredients = db.ingredientsDao().getAllIngredients();
        List<StepsEntity> allSteps = db.stepsDao().getAllSteps();
        if (allIngredients == null || allIngredients.isEmpty() || allSteps == null || allSteps.isEmpty())
            return null;

        for (RecipeEntity recipeEntity : allRecipes) {
            Recipe recipe = new Recipe();
            recipe.id = recipeEntity.id;
            recipe.name = recipeEntity.name;
            recipe.servings = recipeEntity.servings;
            recipe.image = recipeEntity.image;

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for (IngredientsEntity ingredientsEntity : allIngredients) {
                if (ingredientsEntity.recipeId.equals(recipeEntity.id)) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.quantity = ingredientsEntity.quantity;
                    ingredient.measure = ingredientsEntity.measure;
                    ingredient.ingredient = ingredientsEntity.ingredient;
                    ingredients.add(ingredient);
                }
            }
            recipe.ingredients = ingredients;

            ArrayList<Step> steps = new ArrayList<>();
            for (StepsEntity stepsEntity : allSteps) {
                if (stepsEntity.recipeId.equals(recipeEntity.id)) {
                    Step step = new Step();
                    step.id = stepsEntity.stepId;
                    step.shortDescription = stepsEntity.shortDescription;
                    step.description = stepsEntity.description;
                    step.videoURL = stepsEntity.videoUrl;
                    step.thumbnailURL = stepsEntity.thumbnailUrl;
                    steps.add(step);
                }
            }
            recipe.steps = steps;

            recipes.add(recipe);
        }
        return recipes;
    }
}
