package com.cheadtech.example.bakingtime.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.database.DatabaseUtil;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.widgets.BakingTimeWidgetProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BakingTimeIntentService extends IntentService {
    private static final String logTag = BakingTimeIntentService.class.getSimpleName();

    public static final String ACTION_UPDATE_WIDGETS = "com.cheadtech.example.bakingtime.services.action.UPDATE_WIDGET";

    public static final String ACTION_OPEN_RECIPE = "com.cheadtech.example.bakingtime.services.action.OPEN_RECIPE";
    public static final String EXTRA_RECIPE = "com.cheadtech.example.bakingtime.services.extra.EXTRA_RECIPE";

    public BakingTimeIntentService() {
        super("BakingTimeIntentService");
    }

    // TODO: Customize helper method
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, BakingTimeIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    public static void startActionOpenRecipe(Context context, Recipe recipe) {
        Intent intent = new Intent(context, BakingTimeIntentService.class);
        intent.setAction(ACTION_OPEN_RECIPE);
        intent.putExtra(EXTRA_RECIPE, recipe);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action == null) {
                Log.e(logTag, "*** NO ACTION FOUND IN INTENT ***");
                return;
            }
            Log.d(logTag, "Attempting to handle action: " + action);
            switch (action) {
                case ACTION_UPDATE_WIDGETS:
                    handleActionUpdateWidgets();
                    break;
                case ACTION_OPEN_RECIPE:
                    final Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);
                    handleActionOpenRecipe(recipe);
                    break;
                default:
                    Log.e(logTag, "*** INVALID INTENT ACTION ***");
            }
        }
    }

    private void handleActionUpdateWidgets() {
        Log.d(logTag, "Handling action: " + ACTION_UPDATE_WIDGETS);
        new Thread(() -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingTimeWidgetProvider.class));
            BakingTimeWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds,
                    DatabaseUtil.lookupAllRecipes(DatabaseLoader.getDbInstance(this))
            );
        }).start();
    }

    private void handleActionOpenRecipe(Recipe recipe) {
        Log.d(logTag, "Handling action: " + ACTION_OPEN_RECIPE);
        // TODO: Handle action
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
