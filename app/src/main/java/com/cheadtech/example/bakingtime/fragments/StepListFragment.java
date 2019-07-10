package com.cheadtech.example.bakingtime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.StepDetailActivity;
import com.cheadtech.example.bakingtime.adapters.StepListAdapter;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.viewmodels.StepListViewModel;

import java.util.ArrayList;
import java.util.List;

public class StepListFragment extends Fragment {
    private final String tag = this.getClass().toString();

    private TextView ingredientsTV;
    private RecyclerView stepsRV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.step_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            Log.e(tag, "Activity is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        int recipeId = -1;
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null)
            recipeId = extras.getInt(getString(R.string.extra_recipe_id), -1);

        ingredientsTV = view.findViewById(R.id.ingredients_tv);
        stepsRV = view.findViewById(R.id.steps_rv);
        if (ingredientsTV == null || stepsRV == null || recipeId == -1) {
            Log.e(tag, "One or more views is null, or invalid recipe ID");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        initViewModel(recipeId);

        // The top-level ScrollView starts out scrolled past the ingredient card. The following code corrects for this.
        NestedScrollView scrollView = view.findViewById(R.id.step_list_scroller);
        if (scrollView != null) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
        }
    }

    private void initViewModel(int recipeId) {
        StepListViewModel viewModel = ViewModelProviders.of(this).get(StepListViewModel.class);
        viewModel.recipeLiveData.observe(this, recipe -> {
            populateIngredients(recipe);
            populateSteps(recipe);
        });
        viewModel.init(DatabaseLoader.getDbInstance(requireContext()), recipeId, () ->
                Toast.makeText(requireContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show());
    }

    private void populateIngredients(Recipe recipe) {
        SpannableStringBuilder ingredientsBuilder = new SpannableStringBuilder();
        List<Ingredient> ingredients = recipe.ingredients;
        for (Ingredient ingredient : ingredients) {
            SpannableStringBuilder builder = new SpannableStringBuilder(trimTrailingZeroes(ingredient.quantity.toString()));
            if (!ingredient.measure.equals("UNIT"))
                builder.append(" ").append(ingredient.measure);
            builder.append(" ").append(ingredient.ingredient).append("\n");
            builder.setSpan(new BulletSpan(12, ingredientsTV.getCurrentTextColor()), 0, builder.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ingredientsBuilder.append(builder);
        }

        // trim off the trailing new line character
        if (ingredientsBuilder.toString().endsWith("\n"))
            ingredientsTV.setText(ingredientsBuilder.delete(ingredientsBuilder.length() - 1, ingredientsBuilder.length()));
        else
            ingredientsTV.setText(ingredientsBuilder);

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

    private void populateSteps(Recipe recipe) {
        stepsRV.setAdapter(new StepListAdapter(new ArrayList<>(recipe.steps), stepPosition -> {
            // TODO - load step detail fragment if on a tablet???
            Intent intent = new Intent(getContext(), StepDetailActivity.class);
            intent.putExtra(getString(R.string.extra_recipe), recipe);
            intent.putExtra(getString(R.string.extra_recipe_step), stepPosition);
            startActivity(intent);
        }));
    }
}
