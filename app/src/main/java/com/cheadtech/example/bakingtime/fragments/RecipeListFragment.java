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
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.StepListActivity;
import com.cheadtech.example.bakingtime.adapters.RecipeListAdapter;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.viewmodels.RecipeListViewModel;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment {
    private final String tag = getClass().toString();

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
        if (recipeListRV == null) {
            Log.e(tag, "Error loading view");
            return;
        }
        recipeListRV.setAdapter(new RecipeListAdapter(new ArrayList<>(), recipeId ->
                startActivity(new Intent(requireContext(), StepListActivity.class)
                        .putExtra(getString(R.string.extra_recipe_id), recipeId))));

        initViewModel();

        DatabaseLoader.getDbInstance(getContext())
                .recipesDao()
                .getAllRecipesOrderedByIdLiveData()
                .observe(this, recipes -> viewModel.updateRecipeList(DatabaseLoader.getDbInstance(getContext()), recipes));
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.init(DatabaseLoader.getDbInstance(getContext()), new RecipeListViewModel.RecipeListViewModelCallback() {
            @Override
            public void onNetworkError() {
                Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDBError(String logMessage) {
                Log.e(tag, logMessage);
                Toast.makeText(requireContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onEmptyRecipes() {
                Log.w(tag, "recipe list is empty");
                //                Toast.makeText(requireContext(), "TODO" /* TODO */, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onEmptyIngredients() {
                Log.w(tag, "Error loading ingredients list: List was null or empty");
//                Toast.makeText(requireContext(), "TODO" /* TODO */, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ArrayList<Recipe> recipes) {
                if (recipeListRV != null && recipeListRV.getAdapter() != null) {
                    RecipeListAdapter adapter = (RecipeListAdapter) recipeListRV.getAdapter();
                    adapter.setData(recipes);
                }
            }
        });
    }
}
