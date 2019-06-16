package com.cheadtech.example.bakingtime.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class StepDetailFragment extends Fragment {
    private final String tag = getClass().toString();

    private Recipe recipe;
    private Integer currentRecipeStepPosition = 0;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private TextView stepInstructionsTV;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout videoError;
    private TextView videoErrorMessageTV;

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder mediaStateBuilder;

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
        if (activity == null || playerView == null || stepInstructionsTV == null || bottomNavigationView == null ||
                videoErrorMessageTV == null || videoError == null) {
            Log.e(tag, "NULL Activity or View");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            if (activity != null)
                activity.finish();
            return;
        }

        Bundle extras = activity.getIntent().getExtras();
        if (extras == null || !extras.containsKey(getString(R.string.extra_recipe_id)) || !extras.containsKey(getString(R.string.extra_recipe_step))) {
            Log.e(tag, "Error loading data from extras bundle");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        if (extras.getParcelable(getString(R.string.extra_recipe_id)) instanceof Recipe)
            recipe = extras.getParcelable(getString(R.string.extra_recipe_id));
        currentRecipeStepPosition = extras.getInt(getString(R.string.extra_recipe_step), -1);
        if (recipe == null || currentRecipeStepPosition == -1) {
            Log.e(tag, "Recipe or currentRecipeStepPosition not found in intent Extras");
            Toast.makeText(requireContext(), getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show();
            return;
        }

        initializeMediaSession();

        setupStepNavigation();
        stepInstructionsTV.setText(recipe.steps.get(currentRecipeStepPosition).description);
        initPlayer(Uri.parse(recipe.steps.get(currentRecipeStepPosition).videoURL));
    }

    private void initializeMediaSession() {
        mediaSession = new MediaSessionCompat(requireContext(), getString(R.string.app_name));
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);
        mediaStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mediaSession.setPlaybackState(mediaStateBuilder.build());
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (player !=null)
                    player.setPlayWhenReady(true);
            }

            @Override
            public void onPause() {
                if (player !=null)
                    player.setPlayWhenReady(false);
            }

            @Override
            public void onSkipToPrevious() {
                if (player != null)
                    player.seekTo(0);
            }
        });
        mediaSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.setActive(false);
    }

    private void releasePlayer() {
        player.stop();
        player.release();
        player = null;
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

    private void initPlayer(Uri videoUri) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(requireContext(), new DefaultTrackSelector(), new DefaultLoadControl());
            playerView.setPlayer(player);
        } else {
            player.setPlayWhenReady(false);
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireContext(),
                Util.getUserAgent(requireContext(), getString(R.string.app_name)));
        ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
        showPlayer();

        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
                    Log.d(tag, "playing");
                    mediaStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1f);
                } else if (playbackState == ExoPlayer.STATE_READY) {
                    Log.d(tag, "paused");
                    mediaStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1f);
                } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Log.d(tag, "buffering");
                    mediaStateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, player.getCurrentPosition(), 1f);
                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    Log.d(tag, "ended");
                    mediaStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, player.getCurrentPosition(), 1f);
                } else {
                    Log.d(tag, "paused by app");
                    mediaStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1f);
                }
                mediaSession.setPlaybackState(mediaStateBuilder.build());
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    IOException cause = error.getSourceException();
                    if (cause instanceof FileDataSource.FileDataSourceException) {
                        FileDataSource.FileDataSourceException sourceError = (FileDataSource.FileDataSourceException) cause;
                        Log.e(tag, sourceError.getMessage());
                        Activity activity = getActivity();
                        if (activity != null)
                            hidePlayer(getString(R.string.video_not_provided));
                    } else {
                        // Other causes can be checked for here. For this exercise, no further tests will be made.
                        hidePlayer(getString(R.string.video_playback_error));
                    }
                } else {
                    // Other causes can be checked for here. For this exercise, no further tests will be made.
                    hidePlayer(getString(R.string.video_playback_error));
                }
            }
        });
        player.prepare(videoSource);
    }

    private void setupStepNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().getItem(0).setEnabled(currentRecipeStepPosition > 0);
            bottomNavigationView.getMenu().getItem(1).setEnabled(currentRecipeStepPosition < recipe.steps.size() - 1);

            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                if (menuItem.getTitle().equals(getString(R.string.next_step)))
                    currentRecipeStepPosition++;
                else if (menuItem.getTitle().equals(getString(R.string.previous_step)))
                    currentRecipeStepPosition--;

                // disable "previous" and "next" nav menu options if the bounds of the step array are reached
                bottomNavigationView.getMenu().getItem(0).setEnabled(currentRecipeStepPosition > 0);
                bottomNavigationView.getMenu().getItem(1).setEnabled(currentRecipeStepPosition < recipe.steps.size() - 1);

                stepInstructionsTV.setText(recipe.steps.get(currentRecipeStepPosition).description);
                initPlayer(Uri.parse(recipe.steps.get(currentRecipeStepPosition).videoURL));

                return true;
            });
        }
    }
}
