package com.cheadtech.example.bakingtime.viewmodels;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.IngredientsEntity;
import com.cheadtech.example.bakingtime.database.RecipeEntity;
import com.cheadtech.example.bakingtime.database.StepsEntity;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;
import com.cheadtech.example.bakingtime.network.RecipeService;
import com.cheadtech.example.bakingtime.network.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends ViewModel {
    private final String tag = getClass().toString();

    private BakingTimeDB db;

    public final MutableLiveData<ArrayList<Recipe>> recipesLiveData = new MutableLiveData<>();

    public interface RecipeListViewModelCallback {
        void onNetworkError();
        void onDBError(String logMessage);
        void onEmptyRecipes();
        void onEmptyIngredients();
//        void onRecipesUpdated(ArrayList<Recipe> adapterRecipes);
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
            } catch (Exception e) {
                callback.onDBError("Error loading ingredients list:\n\n" + e.getMessage());
            }
        }).start();
    }

    public void updateRecipeList(BakingTimeDB db, List<RecipeEntity> recipes) {
        if (recipes == null || recipes.size() < 1) {
            callback.onEmptyRecipes();
            return;
        }

        // A handler is needed since a DB error should trigger some sort of user notification in the UI thread
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                ArrayList<IngredientsEntity> allIngredients = (ArrayList<IngredientsEntity>) db.ingredientsDao().getAllIngredients();

                if (allIngredients == null || allIngredients.isEmpty()) {
                    handler.post(() -> callback.onEmptyIngredients());
                    return;
                }

                ArrayList<Recipe> adapterRecipes = new ArrayList<>();
                for (RecipeEntity dbRecipe : recipes) {
                    Recipe adapterRecipe = new Recipe();
                    adapterRecipe.id = dbRecipe.id;
                    adapterRecipe.name = dbRecipe.name;
                    adapterRecipe.image = dbRecipe.image;
                    adapterRecipe.servings = dbRecipe.servings;

                    ArrayList<Ingredient> adapterIngredients = new ArrayList<>();
                    for (IngredientsEntity dbIngredient : allIngredients) {
                        if (dbIngredient.recipeId.equals(adapterRecipe.id)) {
                            Ingredient adapterIngredient = new Ingredient();
                            adapterIngredient.quantity = dbIngredient.quantity;
                            adapterIngredient.measure = dbIngredient.measure;
                            adapterIngredient.ingredient = dbIngredient.ingredient;
                            adapterIngredients.add(adapterIngredient);
                        }
                    }
                    adapterRecipe.ingredients = adapterIngredients;
                    adapterRecipes.add(adapterRecipe);
                }
                recipesLiveData.postValue(adapterRecipes);
            } catch (Exception e) {
                handler.post(() -> callback.onDBError("Error loading ingredients list:\n\n" + e.getMessage()));
            }
        }).start();
    }
}
