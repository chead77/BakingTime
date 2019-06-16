package com.cheadtech.example.bakingtime.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.cheadtech.example.bakingtime.database.Recipes;
import com.cheadtech.example.bakingtime.viewmodels.RecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {
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

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        recipeListRV = view.findViewById(R.id.recipe_list_rv);
        if (recipeListRV != null) {
            recipeListRV.setAdapter(new RecipeListAdapter(new ArrayList<>(), recipe -> {
                Intent intent = new Intent(requireContext(), StepListActivity.class);
                intent.putExtra(getString(R.string.extra_recipe_id), recipe);
                startActivity(intent);
            }));
        }

        DatabaseLoader.getDbInstance(getContext()).recipesDao().getAllRecipesLiveData().observe(this, this::updateRecipeList);
//        viewModel.recipeListLiveData.observe(this, recipes -> {
//            if (recipes != null) {
//                updateRecipeList(recipes);
//            } else {
//                Toast.makeText(requireContext(), "Recipe list null", Toast.LENGTH_LONG).show();
//            }
//        });

        initViewModel();
    }

    private void initViewModel() {
        viewModel.init(DatabaseLoader.getDbInstance(getContext()), new RecipeListViewModel.RecipeListViewModelCallback() {
            @Override
            public void onNetworkError() {
                Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDBError() {
                Toast.makeText(requireContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateRecipeList(@NonNull List<Recipes> recipes) {
        if (recipeListRV != null) {
            RecipeListAdapter adapter = (RecipeListAdapter) recipeListRV.getAdapter();
            if (adapter != null) {
                adapter.setData(new ArrayList<>(recipes));
            }
        }
    }
}
