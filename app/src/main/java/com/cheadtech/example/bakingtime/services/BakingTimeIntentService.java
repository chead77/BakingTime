package com.cheadtech.example.bakingtime.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.database.DatabaseUtil;
import com.cheadtech.example.bakingtime.widgets.BakingTimeWidgetProvider;

public class BakingTimeIntentService extends IntentService {
    private static final String logTag = BakingTimeIntentService.class.getSimpleName();

    public static final String ACTION_UPDATE_WIDGETS = "com.cheadtech.example.bakingtime.services.action.UPDATE_WIDGET";

    public BakingTimeIntentService() {
        super("BakingTimeIntentService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, BakingTimeIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
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
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            } else {
                Log.e(logTag, "*** INVALID INTENT ACTION ***");
            }
        }
    }

    private void handleActionUpdateWidgets() {
        new Thread(() -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingTimeWidgetProvider.class));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.recipes_grid);

            BakingTimeWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds,
                    DatabaseUtil.lookupAllRecipes(DatabaseLoader.getDbInstance(this))
            );
        }).start();
    }
}
