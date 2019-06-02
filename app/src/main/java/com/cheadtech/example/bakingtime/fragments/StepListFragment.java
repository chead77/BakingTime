package com.cheadtech.example.bakingtime.fragments;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.adapters.StepListAdapter;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;

import java.util.ArrayList;
import java.util.List;

public class StepListFragment extends Fragment {
    private Recipe recipe;
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

        FragmentActivity activity = getActivity();
        if (activity != null) {
            Bundle extras = activity.getIntent().getExtras();
            if (extras != null && extras.containsKey(getString(R.string.extra_recipe)))
                recipe = extras.getParcelable(getString(R.string.extra_recipe));
        }
        ingredientsTV = view.findViewById(R.id.ingredients_tv);
        stepsRV = view.findViewById(R.id.steps_rv);

        if (recipe != null && ingredientsTV != null && stepsRV != null) {
            populateIngredients();
            populateSteps();
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void populateIngredients() {
        SpannableStringBuilder ingredientsBuilder = new SpannableStringBuilder("");
        List<Ingredient> ingredients = recipe.ingredients;
        for (Ingredient ingredient : ingredients) {
            SpannableStringBuilder builder = new SpannableStringBuilder(ingredient.quantity.toString())
                    .append(" ").append(ingredient.measure)
                    .append(" ").append(ingredient.ingredient)
                    .append("\n");
            builder.setSpan(new BulletSpan(12, ingredientsTV.getCurrentTextColor()), 0, builder.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ingredientsBuilder.append(builder);
        }
        ingredientsTV.setText(ingredientsBuilder);
    }

    private void populateSteps() {
        stepsRV.setAdapter(new StepListAdapter(new ArrayList<>(recipe.steps), new StepListAdapter.StepListAdapterCallback() {
            @Override
            public void onStepClicked(Step step) {
                Toast.makeText(requireContext(), "Clicked: " + step.id, Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
