package com.cheadtech.example.bakingtime.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class StepDetailViewModel extends ViewModel {
    private final String tag = getClass().toString();
    private SimpleExoPlayer player;

    public interface StepDetailViewModelCallback {
        void onNetworkError();
    }
    private StepDetailViewModelCallback callback;

    public void init(SimpleExoPlayer player, StepDetailViewModelCallback callback) {
        this.player = player;
        this.callback = callback;
        refreshVideo();
    }

    private void refreshVideo() {
    }
}
