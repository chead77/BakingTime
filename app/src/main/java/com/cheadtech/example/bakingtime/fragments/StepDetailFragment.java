package com.cheadtech.example.bakingtime.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
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
import androidx.lifecycle.ViewModelProviders;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.viewmodels.StepDetailViewModel;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StepDetailFragment extends Fragment {
    private final String tag = getClass().toString();

    private PlayerView playerView;
    private TextView stepInstructionsTV;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout videoError;
    private TextView videoErrorMessageTV;

    private StepDetailViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.step_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            Log.e(tag, "Activity is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        playerView = view.findViewById(R.id.video_player);
        stepInstructionsTV = view.findViewById(R.id.step_instruction_tv);
        videoError = view.findViewById(R.id.video_error);
        videoErrorMessageTV = view.findViewById(R.id.video_error_message_tv);
        bottomNavigationView = view.findViewById(R.id.step_navigation);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null) {
            Log.e(tag, "Error loading data from extras bundle");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        Recipe recipe = extras.getParcelable(getString(R.string.extra_recipe));
        int currentRecipeStepPosition = extras.getInt(getString(R.string.extra_selected_step_index), -1);
        if (recipe == null || currentRecipeStepPosition == -1) {
            Log.e(tag, "Recipe or current recipe step position not found in intent Extras");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        viewModel = ViewModelProviders.of(this).get(StepDetailViewModel.class);
        viewModel.videoUrlLiveData.observe(this, this::setMediaSource);
        viewModel.previousEnabledLiveData.observe(this, this::setNavigationPreviousEnabled);
        viewModel.nextEnabledLiveData.observe(this, this::setNavigationNextEnabled);
        viewModel.descriptionLiveData.observe(this, this::setDescription);

        playerView.setPlayer(ExoPlayerFactory.newSimpleInstance(requireContext(), new DefaultTrackSelector(), new DefaultLoadControl()));

        viewModel.init(
                recipe,
                currentRecipeStepPosition,
                (SimpleExoPlayer) playerView.getPlayer(),
                new MediaSessionCompat(requireContext(), getString(R.string.app_name)),
                this::onPlayerError);

        setupStepNavigation();
        if (stepInstructionsTV != null)
            stepInstructionsTV.setText(recipe.steps.get(currentRecipeStepPosition).description);
    }

    private void setMediaSource(String videoUrl) {
        showPlayer();
        viewModel.setMediaSource(new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(requireContext(), getString(R.string.app_name))))
                .createMediaSource(Uri.parse(videoUrl)));
    }

    private void setDescription(String description) {
        if (stepInstructionsTV != null)
            stepInstructionsTV.setText(description);
    }

    private void onPlayerError(int errorId) {
        // make sure a context exists, in case the fragment has been dismissed before the player errors out
        if (getContext() != null)
            switch (errorId) {
                case StepDetailViewModel.VIDEO_NOT_PROVIDED:
                    hidePlayer(getString(R.string.error_video_not_provided));
                    break;
                case StepDetailViewModel.VIDEO_PLAYBACK_ERROR:
                    hidePlayer(getString(R.string.error_video_playback_error));
                    break;
                default:
                    hidePlayer(getString(R.string.error_video_playback_error));
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null && playerView.getPlayer() != null)
            viewModel.pausePlayback(playerView.getPlayer().getPlayWhenReady());
    }

    private void showPlayer() {
        if (videoError != null && playerView != null) {
            videoError.setVisibility(View.GONE);
            playerView.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayer(String errorMessage) {
        if (videoError != null && playerView != null && videoErrorMessageTV != null) {
            videoErrorMessageTV.setText(errorMessage);
            videoError.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.GONE);
        }
    }

    private void setupStepNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                if (menuItem.getItemId() == R.id.next_step)
                    viewModel.nextStep();
                else if (menuItem.getItemId() == R.id.previous_step)
                    viewModel.previousStep();
                return true;
            });
        }
    }

    private void setNavigationPreviousEnabled(Boolean previousEnabled) {
        if (bottomNavigationView != null)
            bottomNavigationView.getMenu().getItem(0).setEnabled(previousEnabled);
    }

    private void setNavigationNextEnabled(Boolean nextEnabled) {
        if (bottomNavigationView != null)
            bottomNavigationView.getMenu().getItem(1).setEnabled(nextEnabled);
    }
}
