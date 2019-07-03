package com.cheadtech.example.bakingtime.viewmodels;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cheadtech.example.bakingtime.models.Recipe;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;

// The player and media session logic is included in the ViewModel to help maintain
// playback position and play/pause status during orientation changes.
public class StepDetailViewModel extends ViewModel {
    private final String tag = getClass().toString();

    // ERROR ID CONSTANTS
    public static final int VIDEO_NOT_PROVIDED = 0x00A;
    public static final int VIDEO_PLAYBACK_ERROR = 0x00B;

    private SimpleExoPlayer player;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder mediaStateBuilder;
    private long currentPlayerPosition = 0;
    private boolean onPausePlayWhenReady = true;

    private Recipe recipe;
    private Integer currentRecipeStepPosition = -1;

    public final MutableLiveData<String> videoUrlLiveData = new MutableLiveData<>();
    public final MutableLiveData<Boolean> previousEnabledLiveData = new MutableLiveData<>();
    public final MutableLiveData<Boolean> nextEnabledLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> descriptionLiveData = new MutableLiveData<>();

    public interface StepDetailViewModelCallback {
        void onMediaError(int errorId);
    }
    private StepDetailViewModelCallback callback;

    public void init(
            @NonNull Recipe recipe,
            int currentRecipeStepPosition,
            @NonNull SimpleExoPlayer player,
            @NonNull MediaSessionCompat mediaSession,
            @NonNull StepDetailViewModelCallback callback) {
        this.recipe = recipe;
        if (this.currentRecipeStepPosition == -1)
            this.currentRecipeStepPosition = currentRecipeStepPosition;
        refreshUIFields();

        this.player = player;
        this.callback = callback;
        initMediaSession(mediaSession);
    }

    private void refreshUIFields() {
        videoUrlLiveData.postValue(recipe.steps.get(currentRecipeStepPosition).videoURL);
        previousEnabledLiveData.postValue(currentRecipeStepPosition > 0);
        nextEnabledLiveData.postValue(currentRecipeStepPosition < recipe.steps.size() - 1);
        descriptionLiveData.postValue(recipe.steps.get(currentRecipeStepPosition).description);
    }

    private void initMediaSession(MediaSessionCompat newMediaSession) {
        if (mediaSession == null) {
            mediaSession = newMediaSession;
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
                public void onPlay() { if (player !=null) player.setPlayWhenReady(true); }

                @Override
                public void onPause() { if (player !=null) player.setPlayWhenReady(false); }

                @Override
                public void onSkipToPrevious() { if (player != null) player.seekTo(0); }
            });
        }
        mediaSession.setActive(true);
    }

    public void setMediaSource(@NonNull ProgressiveMediaSource mediaSource) {
        player.setPlayWhenReady(false);
        player.prepare(mediaSource);
        player.seekTo(currentPlayerPosition);
        player.setPlayWhenReady(onPausePlayWhenReady);
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
                Log.w(tag, error.getSourceException().getMessage());
                if (error.type == ExoPlaybackException.TYPE_SOURCE)
                    callback.onMediaError(VIDEO_NOT_PROVIDED);
                else
                    callback.onMediaError(VIDEO_PLAYBACK_ERROR);
            }
        });
    }

    public void pausePlayback(boolean playWhenReady) {
        if (player != null) {
            onPausePlayWhenReady = playWhenReady;
            currentPlayerPosition = player.getCurrentPosition();// TODO might be able to move this to onPlayerStateChanged below, just after the if tree
            player.setPlayWhenReady(false);
        }
    }

    public void nextStep() {
        currentRecipeStepPosition++;
        refreshUIFields();
    }

    public void previousStep() {
        currentRecipeStepPosition--;
        refreshUIFields();
    }
}
