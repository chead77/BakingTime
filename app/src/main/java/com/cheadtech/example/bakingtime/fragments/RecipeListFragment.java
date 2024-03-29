package com.cheadtech.example.bakingtime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.RecipeDetailActivity;
import com.cheadtech.example.bakingtime.adapters.RecipeListAdapter;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.services.BakingTimeIntentService;
import com.cheadtech.example.bakingtime.viewmodels.RecipeListViewModel;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment {
    private static final String logTag = RecipeListFragment.class.getSimpleName();

    private RecyclerView recipeListRV;

    private RecipeListViewModel viewModel;

    public RecipeListFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recipeListRV = view.findViewById(R.id.recipe_list_rv);
        if (recipeListRV != null) {
            recipeListRV.setAdapter(new RecipeListAdapter(new ArrayList<>(), recipe ->
                    startActivity(new Intent(requireContext(), RecipeDetailActivity.class)
                            .putExtra(getString(R.string.extra_recipe), recipe))));

            if (getResources().getBoolean(R.bool.tablet_format)) {
                GridLayoutManager layoutManager = (GridLayoutManager) recipeListRV.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.setSpanCount(3);
                }
            }
        }

        initViewModel();

        DatabaseLoader.getDbInstance(getContext())
                .recipesDao()
                .getAllRecipesWithListsLiveData()
                .observe(this, recipes -> viewModel.updateRecipeList(recipes));
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.recipesLiveData.observe(this, this::onRecipesUpdated);
        viewModel.init(DatabaseLoader.getDbInstance(getContext()), new RecipeListViewModel.RecipeListViewModelCallback() {
            @Override
            public void onNetworkError() {
                if (getContext() != null)
                    Toast.makeText(getContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onDBError() {
                if (getContext() != null)
                    Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDBRefreshed() {
                if (getContext() != null)
                    BakingTimeIntentService.startActionUpdateWidgets(getContext().getApplicationContext());
            }
        });
    }

    private void onRecipesUpdated(ArrayList<Recipe> recipes) {
        if (recipeListRV != null && recipeListRV.getAdapter() != null) {
            RecipeListAdapter adapter = (RecipeListAdapter) recipeListRV.getAdapter();
            adapter.setData(recipes);
        }
    }
}
