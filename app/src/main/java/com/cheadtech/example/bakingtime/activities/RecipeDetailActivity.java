package com.cheadtech.example.bakingtime.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentManager;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.fragments.RecipeDetailFragment;
import com.cheadtech.example.bakingtime.fragments.StepDetailFragment;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.util.StepSelectionInterface;

public class RecipeDetailActivity extends AppCompatActivity implements StepSelectionInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getResources().getBoolean(R.bool.tablet_format)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe_detail_activity);

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(getString(R.string.extra_recipe))) {
            Toast.makeText(this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Recipe recipe = extras.getParcelable(getString(R.string.extra_recipe));
        if (recipe != null) setTitle(recipe.name);

        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeDetailFragment recipeDetailFragment =
                (RecipeDetailFragment) fragmentManager.findFragmentById(R.id.recipe_detail_fragment);
        if (recipeDetailFragment != null)
            recipeDetailFragment.setStepSelectionCallback(this);

        // tablet code
        if (getResources().getBoolean(R.bool.tablet_format)) {
            if (savedInstanceState == null) {
                getIntent().putExtra(getString(R.string.extra_selected_step_index), 0);
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                View view = stepDetailFragment.getView();
                if (view != null && view.findViewById(R.id.step_navigation) != null)
                    view.findViewById(R.id.step_navigation).setVisibility(View.GONE);
                fragmentManager.beginTransaction()
                        .add(R.id.step_detail_frame, stepDetailFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectedStepChanged(int stepIndex) {
        if (getResources().getBoolean(R.bool.tablet_format)) {
            getIntent().putExtra(getString(R.string.extra_selected_step_index), stepIndex);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_frame, new StepDetailFragment())
                    .commit();
        } else {
            Recipe recipe = getIntent().getExtras() != null ? getIntent().getExtras().getParcelable(getString(R.string.extra_recipe)) : null;
            if (recipe != null) {
                Intent intent = new Intent(this, StepDetailActivity.class);
                intent.putExtra(getString(R.string.extra_recipe), recipe);
                intent.putExtra(getString(R.string.extra_selected_step_index), stepIndex);
                startActivity(intent);
            }
        }
    }
}
