package com.cheadtech.example.bakingtime.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.database.BakingTimeDB;
import com.cheadtech.example.bakingtime.database.DatabaseUtil;
import com.cheadtech.example.bakingtime.database.RecipeModel;
import com.cheadtech.example.bakingtime.models.Recipe;
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
        void onDBError();
        void onDBRefreshed();
    }
    private RecipeListViewModelCallback callback;

    public void init(BakingTimeDB dbInstance, RecipeListViewModelCallback callback) {
        this.db = dbInstance;
        this.callback = callback;
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        ServiceLocator.getInstance().getRecipeService().getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
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
            if (!DatabaseUtil.refreshTables(db, recipes)) {
                callback.onDBError();
                return;
            }
            callback.onDBRefreshed();
        }).start();
    }

    public void updateRecipeList(List<RecipeModel> recipes) {
        new Thread(() -> recipesLiveData.postValue(DatabaseUtil.processRecipeModels(recipes))).start();
    }
}
