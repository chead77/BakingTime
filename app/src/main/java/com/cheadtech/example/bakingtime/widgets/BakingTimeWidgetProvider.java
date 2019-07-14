package com.cheadtech.example.bakingtime.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.RecipeListActivity;
import com.cheadtech.example.bakingtime.activities.RecipeDetailActivity;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.services.BakingTimeIntentService;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {
    private static final String logTag = BakingTimeWidgetProvider.class.getSimpleName();

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, @Nullable ArrayList<Recipe> recipes) {
        RemoteViews views;

        if (recipes != null && recipes.size() > 0) {
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            if (width < 200)
                views = getSingleImageRemoteView(context);
            else
                views = getRecipesGridRemoteView(context);
        } else {
            Log.w(logTag, "No recipes found");
            views = getSingleImageRemoteView(context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    // This is called every time a widget is created or updated
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakingTimeIntentService.startActionUpdateWidgets(context);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, @Nullable ArrayList<Recipe> recipes) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipes);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        BakingTimeIntentService.startActionUpdateWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static RemoteViews getSingleImageRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        views.setOnClickPendingIntent(R.id.widget_title_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));
        views.setOnClickPendingIntent(R.id.empty_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));

        return views;
    }

    private static RemoteViews getRecipesGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        views.setRemoteAdapter(R.id.recipes_grid, new Intent(context, GridWidgetService.class));
        views.setPendingIntentTemplate(R.id.recipes_grid, PendingIntent.getActivity(context, 0, new Intent(context, RecipeDetailActivity.class), 0));
        views.setEmptyView(R.id.recipes_grid, R.id.empty_view);

        views.setOnClickPendingIntent(R.id.widget_title_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));
        views.setOnClickPendingIntent(R.id.empty_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));

        return views;
    }
}
