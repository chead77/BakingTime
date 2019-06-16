package com.cheadtech.example.bakingtime.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.Ingredients;
import com.cheadtech.example.bakingtime.database.Recipes;
import com.cheadtech.example.bakingtime.database.Steps;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;
import com.cheadtech.example.bakingtime.network.RecipeService;
import com.cheadtech.example.bakingtime.network.ServiceLocator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends ViewModel {
    private final String tag = getClass().toString();

    private BakingTimeDB db;

    public interface RecipeListViewModelCallback {
        void onNetworkError();
        void onDBError();
    }
    private RecipeListViewModelCallback callback;

    public void init(BakingTimeDB dbInstance, RecipeListViewModelCallback callback) {
        this.db = dbInstance;
        this.callback = callback;
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        RecipeService service = ServiceLocator.getInstance().getRecipeService();
        service.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.code() == 200) {
                    ArrayList<Recipe> responseBody = response.body();
                    if (responseBody != null) {
                        refreshDB(responseBody);
                        return;
                    } else {
                        Log.e(tag, " - Network response successful, but a null response was received.");
                    }
                } else if (response.errorBody() != null) {
                    Log.e(tag, response.errorBody().toString());
                } else {
                    Log.e(tag, " - Network response was unsuccessful");
                }
                callback.onNetworkError();
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.e(tag, " - Network call failed: " + t.getMessage());
                callback.onNetworkError();
            }
        });
    }

    private void refreshDB(ArrayList<Recipe> recipes) {
        new Thread(() -> {
            try {
                // clear out the recipes, ingredients, and steps table and repopulate with new data.
                db.recipesDao().delete(db.recipesDao().getAllRecipes());
                db.ingredientsDao().delete(db.ingredientsDao().getAllIngredients());
                db.stepsDao().delete(db.stepsDao().getAllSteps());

                ArrayList<Recipes> newRecipes = new ArrayList<>();
                ArrayList<Ingredients> newIngredients = new ArrayList<>();
                ArrayList<Steps> newSteps = new ArrayList<>();

                for (Recipe recipe : recipes) {
                    Recipes newRecipe = new Recipes();
                    newRecipe.id = recipe.id;
                    newRecipe.name = recipe.name;
                    newRecipe.servings = recipe.servings;
                    newRecipe.image = recipe.image;
                    newRecipes.add(newRecipe);

                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredients newIngredient = new Ingredients();
                        newIngredient.recipeId = recipe.id;
                        newIngredient.quantity = ingredient.quantity;
                        newIngredient.measure = ingredient.measure;
                        newIngredient.ingredient = ingredient.ingredient;
                        newIngredients.add(newIngredient);
                    }

                    for (Step step : recipe.steps) {
                        Steps newStep = new Steps();
                        newStep.recipeId = recipe.id;
                        newStep.stepId = step.id;
                        newStep.shortDescription = step.shortDescription;
                        newStep.description = step.description;
                        newStep.thumbnailUrl = step.thumbnailURL;
                        newStep.videoUrl = step.videoURL;
                        newSteps.add(newStep);
                    }
                }
                db.recipesDao().insertAll(newRecipes);
                db.ingredientsDao().insertAll(newIngredients);
                db.stepsDao().insertAll(newSteps);
            } catch (Exception e) {
                Log.e(tag, e.getMessage());
                callback.onDBError();
            }
        }).start();
    }
}
