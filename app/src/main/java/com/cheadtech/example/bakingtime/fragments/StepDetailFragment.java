package com.cheadtech.example.bakingtime.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.models.Step;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment {
    private final String tag = getClass().toString();

    private Recipe recipe;
    private Integer currentRecipeStepPosition = 0;
    private ArrayList<Step> recipeSteps;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private LinearLayout videoError;
    private TextView videoErrorMessageTV;

    private TextView stepInstructionsTV;

    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.step_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        playerView = view.findViewById(R.id.video_player);
        stepInstructionsTV = view.findViewById(R.id.step_instruction_tv);
        videoError = view.findViewById(R.id.video_error);
        videoErrorMessageTV = view.findViewById(R.id.video_error_message_tv);
        bottomNavigationView = view.findViewById(R.id.step_navigation);
        if (activity == null || playerView == null || stepInstructionsTV == null || videoError == null
                || videoErrorMessageTV == null || bottomNavigationView == null) {
            Log.e(tag, "NULL Activity or View");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            if (activity != null)
                activity.finish();
            return;
        }

        Bundle extras = activity.getIntent().getExtras();
        if (extras == null || !extras.containsKey(getString(R.string.extra_recipe)) || !extras.containsKey(getString(R.string.extra_recipe_step))) {
            Log.e(tag, "Error loading data from extras bundle");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        if (extras.getParcelable(getString(R.string.extra_recipe)) instanceof Recipe)
            recipe = extras.getParcelable(getString(R.string.extra_recipe));
        currentRecipeStepPosition = extras.getInt(getString(R.string.extra_recipe_step), -1);
        if (recipe == null || currentRecipeStepPosition == -1) {
            Log.e(tag, "Recipe or currentRecipeStepPosition not found in intent Extras");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        recipeSteps = new ArrayList<>(recipe.steps);
        setupStepNavigation();
        stepInstructionsTV.setText(recipeSteps.get(currentRecipeStepPosition).description);
        resetPlayerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        player.release();
    }

    private void showPlayer() {
        if (videoError == null || playerView == null) {
            Log.e(tag, "showPlayer() - A View is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        videoError.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
    }

    private void hidePlayer(String errorMessage) {
        if (videoError == null || playerView == null || videoErrorMessageTV == null) {
            Log.e(tag, "hidePlayer() - A View is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        videoErrorMessageTV.setText(errorMessage);
        videoError.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
    }

    private void resetPlayerView() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(requireContext());
            playerView.setPlayer(player);
        } else {
            player.setPlayWhenReady(false);
        }
        String url = recipeSteps.get(currentRecipeStepPosition).videoURL;
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireContext(),
                Util.getUserAgent(requireContext(), getString(R.string.app_name)));
        Uri uri = Uri.parse(url);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        showPlayer();
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (recipeSteps.get(currentRecipeStepPosition).videoURL.isEmpty())
                    hidePlayer(getString(R.string.video_not_provided));
                else
                    hidePlayer(getString(R.string.video_playback_error));
            }
        });
        player.prepare(videoSource);
    }

    private void setupStepNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().getItem(0).setEnabled(currentRecipeStepPosition > 0);
            bottomNavigationView.getMenu().getItem(1).setEnabled(currentRecipeStepPosition < recipeSteps.size() - 1);

            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                if (menuItem.getTitle().equals(getString(R.string.next_step)))
                    currentRecipeStepPosition++;
                else if (menuItem.getTitle().equals(getString(R.string.previous_step)))
                    currentRecipeStepPosition--;
                else {
                    Log.e(tag, "Invalid navigation menu option");
                    Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
                    return false;
                }

                // disable "previous" and "next" nav menu options if the bounds of the step array are reached
                bottomNavigationView.getMenu().getItem(0).setEnabled(currentRecipeStepPosition > 0);
                bottomNavigationView.getMenu().getItem(1).setEnabled(currentRecipeStepPosition < recipeSteps.size() - 1);

                stepInstructionsTV.setText(recipeSteps.get(currentRecipeStepPosition).description);
                resetPlayerView();

                return true;
            });
        }
    }
}
