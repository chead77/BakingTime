package com.cheadtech.example.bakingtime.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.RecipeListActivity;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.database.IngredientsEntity;
import com.cheadtech.example.bakingtime.database.RecipeEntity;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.util.BakingTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {
    private final String tag = getClass().toString();

    private static ArrayList<Recipe> recipes;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);

        if (recipes == null || recipes.size() < 1) {
            views.setViewVisibility(R.id.recipes_grid, View.GONE);
            views.setViewVisibility(R.id.fallback_image, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.recipes_grid, View.VISIBLE);
            views.setViewVisibility(R.id.fallback_image, View.GONE);

            // TODO - somehow send the recipes to the grid widget service???
        }

        views.setOnClickPendingIntent(R.id.widget_title_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));
        views.setOnClickPendingIntent(R.id.fallback_image,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // This is called every time a widget is created or updated
        Log.d(tag, "Updating Widgets");

        new Thread(() -> {
            recipes = null;
            try {
                recipes = BakingTimeUtil.getRecipeArrayListFromDB(DatabaseLoader.getDbInstance(context));
            } catch (Exception e) {
                Log.e(tag, e.getMessage());
                // TODO - error handling
            }

            // There may be multiple widgets active, so update all of them
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }).start();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
