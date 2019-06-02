package com.cheadtech.example.bakingtime.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Recipe;

public class StepListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_list_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(getString(R.string.extra_recipe))) {
            Recipe recipe = extras.getParcelable(getString(R.string.extra_recipe));
            if (recipe != null) {
                setTitle(recipe.name);

                // TODO: use the fragment manager to display the media fragment on tablets???
            } else {
                Toast.makeText(this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
