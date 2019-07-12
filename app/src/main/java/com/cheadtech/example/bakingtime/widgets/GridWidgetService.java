package com.cheadtech.example.bakingtime.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.database.IngredientsEntity;
import com.cheadtech.example.bakingtime.database.RecipeEntity;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final String tag = getClass().toString();

    private Context context;
    private BakingTimeDB db;

    private final ArrayList<Recipe> dataSet = new ArrayList<>();

    public GridRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
        db = DatabaseLoader.getDbInstance(context);
    }

    @Override
    public void onCreate() {
        // TODO
    }

    @Override
    public void onDataSetChanged() {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        dataSet.clear();

        // TODO - this might not be where the visibility stuff goes, double check
        if (dataSet.size() < 1) {
            views.setViewVisibility(R.id.recipes_grid, View.GONE);
            views.setViewVisibility(R.id.fallback_image, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.recipes_grid, View.VISIBLE);
            views.setViewVisibility(R.id.fallback_image, View.GONE);
        }
        // TODO - end of todo block

        new Thread(() -> {
            try {
                List<RecipeEntity> allRecipesDB = db.recipesDao().getAllRecipes();
                if (allRecipesDB.size() < 1) {
                    // TODO - should something be done here???
                    return;
                }

                List<IngredientsEntity> allIngredientsDB = db.ingredientsDao().getAllIngredients();
                if (allIngredientsDB.size() < 1) {
                    // TODO - should something be done here???
                    return;
                }

                for (RecipeEntity recipeDB : allRecipesDB) {
                    Recipe widgetRecipe = new Recipe();
                    widgetRecipe.id = recipeDB.id;
                    widgetRecipe.name = recipeDB.name;
                    widgetRecipe.servings = recipeDB.servings;
                    widgetRecipe.image = recipeDB.image;
                    ArrayList<Ingredient> widgetIngredients = new ArrayList<>();
                    for (IngredientsEntity ingredientDB : allIngredientsDB) {
                        if (ingredientDB.recipeId.equals(recipeDB.id)) {
                            Ingredient ingredient = new Ingredient();
                            ingredient.measure = ingredientDB.measure;
                            ingredient.quantity = ingredientDB.quantity;
                            ingredient.ingredient = ingredientDB.ingredient;
                            widgetIngredients.add(ingredient);
                        }
                    }
                    widgetRecipe.ingredients = widgetIngredients;
                    dataSet.add(widgetRecipe);
                }
            } catch (Exception e) {
                Log.e(tag, e.getMessage());
                // TODO - error handling
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        // TODO
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        if (dataSet.size() < 1) return null;

        Recipe recipe = dataSet.get(position);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        // TODO
        return null;
    }

    @Override
    public int getViewTypeCount() {
        // TODO
        return 0;
    }

    @Override
    public long getItemId(int i) {
        // TODO
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO
        return false;
    }
}
