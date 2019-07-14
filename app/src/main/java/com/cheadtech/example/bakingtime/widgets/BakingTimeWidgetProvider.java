package com.cheadtech.example.bakingtime.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.RecipeListActivity;
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
        RemoteViews views = getRecipesGridRemoteView(context);

        if (recipes != null && recipes.size() > 0) {
            Log.d(logTag, recipes.size() + " recipes found");

            // TODO - Do I want to display something other than the grid if the widget gets shrunk too far???
            //  Use appWidgetManager.getappwidgetoptions(appwidgetid);
            //  Followed by options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH?
            //  for now, set up the grid remote view...

            views.setViewVisibility(R.id.recipes_grid, View.VISIBLE);
            views.setViewVisibility(R.id.fallback_image, View.GONE);
        } else {
            Log.w(logTag, "No recipes found");
            views.setViewVisibility(R.id.recipes_grid, View.GONE);
            views.setViewVisibility(R.id.fallback_image, View.VISIBLE);
        }

        views.setOnClickPendingIntent(R.id.widget_title_view,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));
        views.setOnClickPendingIntent(R.id.fallback_image,
                PendingIntent.getActivity(context, 0, new Intent(context, RecipeListActivity.class), 0));

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
        // TODO

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    private static RemoteViews getRecipesGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        // TODO -
        return views;
    }
}
