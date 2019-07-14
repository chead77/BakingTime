package com.cheadtech.example.bakingtime.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.DatabaseUtil;
import com.cheadtech.example.bakingtime.models.Recipe;

public class StepListViewModel extends ViewModel {
    private BakingTimeDB db;
    private int recipeId;

    public MutableLiveData<Recipe> recipeLiveData = new MutableLiveData<>();

    public interface StepListViewModelCallback {
        void onDBError();
    }
    private StepListViewModelCallback callback;

    public void init(@NonNull BakingTimeDB db, int recipeId, StepListViewModelCallback callback) {
        this.db = db;
        this.recipeId = recipeId;
        this.callback = callback;
        loadRecipeComponents();
    }

    private void loadRecipeComponents() {
        new Thread(() -> {
            Recipe recipe = DatabaseUtil.lookupRecipe(db, recipeId);
            if (recipe == null)
                callback.onDBError();
            else
                recipeLiveData.postValue(recipe);
        }).start();
    }
}
