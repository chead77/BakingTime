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

public class StepDetailFragment extends Fragment {
    private final String tag = getClass().toString();

    private Recipe recipe;
    private Integer currentRecipeStep = 0;
    private Step currentStep;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private LinearLayout videoError;
    private TextView videoErrorMessageTV;

    private TextView stepInstructionsTV;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.step_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity == null) {
            Log.e(tag, "Activity not found");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
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
        currentRecipeStep = extras.getInt(getString(R.string.extra_recipe_step), -1);
        if (recipe == null || currentRecipeStep == -1) {
            Log.e(tag, "invalid Recipe or currentRecipeStep");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        playerView = view.findViewById(R.id.video_player);
        stepInstructionsTV = view.findViewById(R.id.step_instruction_tv);
        videoError = view.findViewById(R.id.video_error);
        videoErrorMessageTV = view.findViewById(R.id.video_error_message_tv);
        if (playerView == null || stepInstructionsTV == null || videoError == null || videoErrorMessageTV == null) {
            Log.e(tag, "One or more Views are null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        try {
            currentStep = recipe.steps.get(currentRecipeStep);
        } catch (IndexOutOfBoundsException e) {
            Log.e(tag, e.getMessage());
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        setupPlayerView();
        stepInstructionsTV.setText(currentStep.description);
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

    private void setupPlayerView() {
        String url = currentStep.videoURL;
        player = ExoPlayerFactory.newSimpleInstance(requireContext());
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireContext(),
                Util.getUserAgent(requireContext(), getString(R.string.app_name)));
        Uri uri = Uri.parse(url);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (currentStep.videoURL.isEmpty())
                    hidePlayer(getString(R.string.video_not_provided));
                else
                    hidePlayer(getString(R.string.video_playback_error));
            }
        });
        showPlayer();
        player.prepare(videoSource);
    }

    private void showPlayer() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (videoError == null || playerView == null) {
            Log.e(tag, "showPlayer() - A View is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        videoError.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
    }

    private void hidePlayer(String errorMessage) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (videoError == null || playerView == null || videoErrorMessageTV == null) {
            Log.e(tag, "hidePlayer() - A View is null");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        videoErrorMessageTV.setText(errorMessage);
        videoError.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
    }
}
