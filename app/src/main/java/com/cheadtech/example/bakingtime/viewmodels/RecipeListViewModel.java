package com.cheadtech.example.bakingtime.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.network.RecipeService;
import com.cheadtech.example.bakingtime.network.ServiceLocator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends ViewModel {
    private final String tag = getClass().toString();

    public MutableLiveData<ArrayList<Recipe>> recipeListLiveData = new MutableLiveData<>();

    public interface RecipeListViewModelCallback {
        void onNetworkError();
    }
    private RecipeListViewModelCallback callback;

    public void init(RecipeListViewModelCallback callback) {
        this.callback = callback;
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        RecipeService service = ServiceLocator.getInstance().getRecipeService();
        service.getRecipes().enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        recipeListLiveData.postValue(response.body());
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
}
