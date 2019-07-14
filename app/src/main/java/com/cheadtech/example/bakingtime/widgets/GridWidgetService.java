package com.cheadtech.example.bakingtime.widgets;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.RecipeDetailActivity;
import com.cheadtech.example.bakingtime.activities.RecipeListActivity;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.database.DatabaseUtil;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;

import java.util.ArrayList;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;

    private final ArrayList<Recipe> dataSet = new ArrayList<>();

    GridRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public void onCreate() {
        // do nothing
    }

    @Override
    public void onDataSetChanged() {
        dataSet.clear();
        ArrayList<Recipe> recipeEntities = DatabaseUtil.lookupAllRecipes(DatabaseLoader.getDbInstance(context));
        if (recipeEntities != null && recipeEntities.size() >= 1) {
            dataSet.addAll(recipeEntities);
        }
    }

    @Override
    public void onDestroy() {
        // do nothing
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (dataSet.size() < 1) return null;

        Recipe recipe = dataSet.get(position);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.name_tv, recipe.name);

        SpannableStringBuilder ingredientsBuilder = new SpannableStringBuilder();
        for (Ingredient ingredient : recipe.ingredients) {
            SpannableStringBuilder builder = new SpannableStringBuilder(trimTrailingZeroes(ingredient.quantity.toString()));
            if (!ingredient.measure.equals(context.getString(R.string.measure_unit)))
                builder.append(" ").append(ingredient.measure);
            builder.append(" ").append(ingredient.ingredient).append("\n");
            builder.setSpan(new BulletSpan(12, context.getColor(android.R.color.white)),
                    0, builder.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ingredientsBuilder.append(builder);
        }

        // trim off the trailing new line character
        if (ingredientsBuilder.toString().endsWith("\n"))
            views.setTextViewText(R.id.ingredients_tv, ingredientsBuilder.delete(ingredientsBuilder.length() - 1, ingredientsBuilder.length()));
        else
            views.setTextViewText(R.id.ingredients_tv, ingredientsBuilder);

        Bundle extras = new Bundle();
        extras.putParcelable(context.getString(R.string.extra_recipe), recipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.recipe_widget_item, fillInIntent);

        return views;
    }

    private String trimTrailingZeroes(String quantity) {
        if (quantity.contains(".")) {
            while (quantity.endsWith("0")) {
                quantity = quantity.substring(0, quantity.length() - 1);
            }
            if (quantity.endsWith("."))
                quantity = quantity.substring(0, quantity.length() - 1);
        }
        return quantity;
    }

    @Override
    public RemoteViews getLoadingView() { return null; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public boolean hasStableIds() { return true; }
}
