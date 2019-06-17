package com.cheadtech.example.bakingtime.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.database.DatabaseLoader;

public class StepListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_list_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int recipeId = extras.getInt(getString(R.string.extra_recipe_id), -1);
            if (recipeId != -1) {
                new Thread(() -> {
                    try {
                        setTitle(DatabaseLoader.getDbInstance(StepListActivity.this).recipesDao().getRecipe(recipeId).name);
                    } catch (Exception e) {
                        Log.e(StepListActivity.this.getClass().toString(), "Database error: " + e.getMessage());
                        Toast.makeText(StepListActivity.this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).start();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
