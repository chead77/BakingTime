package com.cheadtech.example.bakingtime.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;

import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////
// Methods accessing the database should not be called from UI/main thread
////////////////////////////////////////////////////////////////////////////
public class DatabaseUtil {
    private static final String logTag = DatabaseUtil.class.getSimpleName();

    public static boolean refreshTables(@NonNull BakingTimeDB db, ArrayList<Recipe> recipes) {
        if (recipes == null)
            return false;

        try {
            // clear out the recipes, ingredients, and steps table and repopulate with new data.
            db.recipesDao().delete(db.recipesDao().getAllRecipes());
            db.ingredientsDao().delete(db.ingredientsDao().getAllIngredients());
            db.stepsDao().delete(db.stepsDao().getAllSteps());

            ArrayList<RecipeEntity> newRecipes = new ArrayList<>();
            ArrayList<IngredientsEntity> newIngredients = new ArrayList<>();
            ArrayList<StepsEntity> newSteps = new ArrayList<>();

            for (Recipe recipe : recipes) {
                RecipeEntity newRecipe = new RecipeEntity();
                newRecipe.id = recipe.id;
                newRecipe.name = recipe.name;
                newRecipe.servings = recipe.servings;
                newRecipe.image = recipe.image;
                newRecipes.add(newRecipe);

                for (Ingredient ingredient : recipe.ingredients) {
                    IngredientsEntity newIngredient = new IngredientsEntity();
                    newIngredient.recipeId = recipe.id;
                    newIngredient.quantity = ingredient.quantity;
                    newIngredient.measure = ingredient.measure;
                    newIngredient.ingredient = ingredient.ingredient;
                    newIngredients.add(newIngredient);
                }

                for (Step step : recipe.steps) {
                    StepsEntity newStep = new StepsEntity();
                    newStep.recipeId = recipe.id;
                    newStep.stepId = step.id;
                    newStep.shortDescription = step.shortDescription;
                    newStep.description = step.description;
                    newStep.thumbnailUrl = step.thumbnailURL;
                    newStep.videoUrl = step.videoURL;
                    newSteps.add(newStep);
                }
            }
            db.ingredientsDao().insertAll(newIngredients);
            db.stepsDao().insertAll(newSteps);
            db.recipesDao().insertAll(newRecipes);

            return true;
        } catch (Exception e) {
            Log.e(logTag, e.getMessage());
            return false;
        }
    }

    @Nullable
    public static ArrayList<Recipe> lookupAllRecipes(@NonNull BakingTimeDB db) {
        Log.d(logTag, "refreshing recipe list with ingredients");

        try {
            List<RecipeModel> recipeModels = db.recipesDao().getAllRecipesWithLists();
            if (recipeModels == null || recipeModels.size() < 1) {
                return null;
            }

            ArrayList<Recipe> recipes = new ArrayList<>();

            for (RecipeModel recipeEntity : recipeModels) {
                Recipe recipe = new Recipe();
                recipe.id = recipeEntity.recipe.id;
                recipe.name = recipeEntity.recipe.name;
                recipe.servings = recipeEntity.recipe.servings;
                recipe.image = recipeEntity.recipe.image;
                recipe.ingredients = new ArrayList<>();
                for (IngredientsEntity ingredientsEntity : recipeEntity.ingredients) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.quantity = ingredientsEntity.quantity;
                    ingredient.measure = ingredientsEntity.measure;
                    ingredient.ingredient = ingredientsEntity.ingredient;
                    recipe.ingredients.add(ingredient);
                }
                recipe.steps = new ArrayList<>();
                for (StepsEntity stepsEntity : recipeEntity.steps) {
                    Step step = new Step();
                    step.id = stepsEntity.stepId;
                    step.shortDescription = stepsEntity.shortDescription;
                    step.description = stepsEntity.description;
                    step.videoURL = stepsEntity.videoUrl;
                    step.thumbnailURL = stepsEntity.thumbnailUrl;
                    recipe.steps.add(step);
                }

                recipes.add(recipe);
            }

            return recipes;
        } catch (Exception e) {
            Log.e(logTag, e.getMessage());
            return null;
        }
    }

    @Nullable
    public static Recipe lookupRecipe(@NonNull BakingTimeDB db, int recipeId) {
        try {
            RecipeModel dbRecipe = db.recipesDao().getRecipeWithLists(recipeId);

            Recipe recipe = new Recipe();
            recipe.id = dbRecipe.recipe.id;
            recipe.name = dbRecipe.recipe.name;
            recipe.servings = dbRecipe.recipe.servings;
            recipe.image = dbRecipe.recipe.image;
            recipe.ingredients = new ArrayList<>();
            recipe.ingredients = new ArrayList<>();
            for (IngredientsEntity dbIngredient : dbRecipe.ingredients) {
                Ingredient ingredient = new Ingredient();
                ingredient.quantity = dbIngredient.quantity;
                ingredient.measure = dbIngredient.measure;
                ingredient.ingredient = dbIngredient.ingredient;
                recipe.ingredients.add(ingredient);
            }
            recipe.steps = new ArrayList<>();
            for (StepsEntity dbStep : dbRecipe.steps) {
                Step step = new Step();
                step.id = dbStep.stepId;
                step.shortDescription = dbStep.shortDescription;
                step.description = dbStep.description;
                step.thumbnailURL = dbStep.thumbnailUrl;
                step.videoURL = dbStep.videoUrl;
                recipe.steps.add(step);
            }

            return recipe;
        } catch (Exception e) {
            Log.e(logTag, "lookupRecipe() - DB error: " + e.getMessage());
            return null;
        }
    }

    public static ArrayList<Recipe> processRecipeModels(List<RecipeModel> recipeModels) {
        if (recipeModels == null || recipeModels.size() < 1) {
            return null;
        }

        ArrayList<Recipe> recipes = new ArrayList<>();

        for (RecipeModel recipeModel : recipeModels) {
            Recipe recipe = new Recipe();
            recipe.id = recipeModel.recipe.id;
            recipe.name = recipeModel.recipe.name;
            recipe.servings = recipeModel.recipe.servings;
            recipe.image = recipeModel.recipe.image;
            recipe.ingredients = new ArrayList<>();
            for (IngredientsEntity ingredientsEntity : recipeModel.ingredients) {
                Ingredient ingredient = new Ingredient();
                ingredient.quantity = ingredientsEntity.quantity;
                ingredient.measure = ingredientsEntity.measure;
                ingredient.ingredient = ingredientsEntity.ingredient;
                recipe.ingredients.add(ingredient);
            }
            recipe.steps = new ArrayList<>();
            for (StepsEntity stepsEntity : recipeModel.steps) {
                Step step = new Step();
                step.id = stepsEntity.stepId;
                step.shortDescription = stepsEntity.shortDescription;
                step.description = stepsEntity.description;
                step.videoURL = stepsEntity.videoUrl;
                step.thumbnailURL = stepsEntity.thumbnailUrl;
                recipe.steps.add(step);
            }

            recipes.add(recipe);
        }

        return recipes;
    }
}
