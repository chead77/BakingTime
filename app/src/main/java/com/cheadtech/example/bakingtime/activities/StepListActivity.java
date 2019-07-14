package com.cheadtech.example.bakingtime.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Recipe;

public class StepListActivity extends AppCompatActivity {
    private final String tag = getClass().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_list_activity);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(getString(R.string.extra_recipe))) {
            Toast.makeText(this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Recipe recipe = extras.getParcelable(getString(R.string.extra_recipe));
        if (recipe != null)
            setTitle(recipe.name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
