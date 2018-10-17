package com.kl;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kl.playback.LocalPlayback;
import com.kl.playback.Playback;

public class KLPlaybackManager implements PlaybackPreparer,
        PlayerControlView.VisibilityListener, Playback.Callback {

    /*
        Following https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c
            HLS -> HlsMediaSource
            DASH -> DashMediaSource
            SS -> SsMediaSource
            MP4 and others -> ExtractorMediaSource
     */


    private Playback mPlayback;

    public Playback getPlayback() {
        return mPlayback;
    }

    // Saved instance state keys.
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

//    private static final CookieManager DEFAULT_COOKIE_MANAGER;
//    static {
//        DEFAULT_COOKIE_MANAGER = new CookieManager();
//        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
//    }

    KLPlaybackManager() {
        // force empty constructor here
    }
    public KLPlaybackManager(Context context) {
        mPlayback = new LocalPlayback(context);
    }

    // for PlaybackPreparer - S
    @Override
    public void preparePlayback() {

    }
    // for PlaybackPreparer - E

    // for PlayerControlView.VisibilityListener - S
    @Override
    public void onVisibilityChange(int visibility) {

    }
    // for PlayerControlView.VisibilityListener - E

    // for Playback.Callback - S
    /**
     * On current music completed.
     */
    @Override
    public void onCompletion() {

    }

    /**
     * on Playback status changed
     * Implementations can use this callback to update
     * playback state on the media sessions.
     *
     * @param state
     */
    @Override
    public void onPlaybackStatusChanged(int state) {

    }

    /**
     * @param error to be added to the PlaybackState
     */
    @Override
    public void onError(String error) {

    }

    /**
     * @param mediaId being currently played
     */
    @Override
    public void setCurrentMediaId(String mediaId) {

    }
    // for Playback.Callback - E


    public void playAudio(String radioUrl) {
        //MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat(
        //        "mediaId", "title", "subtitle",
        //        "description", null, null, null, null);
//        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(null, 10);
//        mPlayback.play(queueItem);

        mPlayback.play(radioUrl);

        /*
        Context context = KLApplication.getInstance().getContext();

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"), bandwidthMeter);
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);

        ExtractorMediaSource mediaSource = factory.createMediaSource(Uri.parse(radioUrl));

        mExoPlayer.prepare(mediaSource);

        // play audio
        mExoPlayer.setPlayWhenReady(true);

        // pause audio
        // player.setPlayWhenReady(false);
        */
    }

    public void releaseResources(boolean releasePlayer) {
        mPlayback.releaseResources(releasePlayer);
    }

}
