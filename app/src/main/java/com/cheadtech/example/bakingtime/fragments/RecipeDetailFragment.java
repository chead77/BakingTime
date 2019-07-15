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
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.activities.StepDetailActivity;
import com.cheadtech.example.bakingtime.adapters.StepListAdapter;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.util.StepSelectionInterface;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {
    private static final String logTag = RecipeDetailFragment.class.getSimpleName();

    private TextView ingredientsTV;
    private RecyclerView stepsRV;

    private Recipe recipe;

    private StepSelectionInterface stepSelectionCallback;
    public void setStepSelectionCallback(StepSelectionInterface callback) {
        stepSelectionCallback = callback;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) return;

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null || !extras.containsKey(getString(R.string.extra_recipe))) {
            Log.e(logTag, "Error retrieving extras");
            Toast.makeText(getContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        recipe = extras.getParcelable(getString(R.string.extra_recipe));

        ingredientsTV = view.findViewById(R.id.ingredients_tv);
        stepsRV = view.findViewById(R.id.steps_rv);
        if (ingredientsTV == null || stepsRV == null || recipe == null) {
            Log.w(logTag, "One or more views is null, or invalid recipe ID");
            getActivity().finish();
            return;
        }

        populateIngredients();
        stepsRV.setAdapter(new StepListAdapter(new ArrayList<>(recipe.steps),
                selectedStepPosition -> stepSelectionCallback.onSelectedStepChanged(selectedStepPosition)));

        // The top-level ScrollView starts out scrolled past the ingredient card. The following code corrects for this.
        NestedScrollView scrollView = view.findViewById(R.id.step_list_scroller);
        if (scrollView != null) scrollView.post(() -> scrollView.scrollTo(0, 0));
    }

    private void populateIngredients() {
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
}
