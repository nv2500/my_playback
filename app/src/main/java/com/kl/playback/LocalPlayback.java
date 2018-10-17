package com.kl.playback;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kl.KLApplication;
import com.kl.utils.Logger;

import java.net.CookieHandler;
import java.util.Formatter;
import java.util.Locale;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

public class LocalPlayback implements Playback {

    private Playback.Callback mCallback;

    private WifiManager.WifiLock mWifiLock;
    private boolean mPlayOnFocusGain;

    // ExoPlayer player
    private SimpleExoPlayer mExoPlayer;

    private AudioManager mAudioManager;

    private String mCurrentMediaId;

    // Whether to return STATE_NONE or STATE_STOPPED when mExoPlayer is null;
    private boolean mExoPlayerNullIsStopped =  false;

    LocalPlayback() {
        // force empty constructor here
    }

    public LocalPlayback(final Context context) {
        initialize(context);
    }

    private synchronized void initialize(final Context context) {
        //Context context = KLApplication.getInstance().getContext();

        // initialize for ExoPlayer
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        mExoPlayer.addListener(mEventListener);

        // for AudioManager
        this.mAudioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // for Wifi lock
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        WifiManager wifiManager = ((WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE));
        if (wifiManager != null) {
            this.mWifiLock = wifiManager
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "uAmp_lock");
        }
    }

    private final ExoPlayerEventListener mEventListener = new ExoPlayerEventListener();
    private final class ExoPlayerEventListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Logger.getLogger().e("onPlayerStateChanged: playWhenReady = "+String.valueOf(playWhenReady)
                    +" playbackState = "+playbackState);
            switch (playbackState){
                case Player.STATE_ENDED:
                    Logger.getLogger().e("Playback ended!");
                    //Stop playback and return to start position
                    //setPlayPause(false);
                    mExoPlayer.seekTo(0);

                    // The media player finished playing the current song.
                    if (mCallback != null) {
                        mCallback.onCompletion();
                    }
                    break;

                case Player.STATE_READY:
                    Logger.getLogger().e("ExoPlayer ready! pos: "+ mExoPlayer.getCurrentPosition()
                            +" max: "+stringForTime((int) mExoPlayer.getDuration()));
                    //setProgress();
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
                case Player.STATE_BUFFERING:
                    Logger.getLogger().e("Playback buffering!");
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
                case Player.STATE_IDLE:
                    Logger.getLogger().e("ExoPlayer idle!");
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;
            }

            Logger.getLogger().e("[INF] ExoPlayer error: what=" + what);
            if (mCallback != null) {
                mCallback.onError("ExoPlayer error " + what);
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }

    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    @Override
    public void start() {
        // Nothing to do
    }

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     *
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    @Override
    public void stop(boolean notifyListeners) {
        releaseResources(true);
    }

    /**
     * Set the latest playback state as determined by the caller.
     *
     * @param state
     */
    @Override
    public void setState(int state) {
        // Nothing to do (mExoPlayer holds its own state).
    }

    /**
     * Get the current {@link PlaybackStateCompat#getState()}
     */
    @Override
    public int getState() {
        if (mExoPlayer == null) {
            return mExoPlayerNullIsStopped
                    ? PlaybackStateCompat.STATE_STOPPED
                    : PlaybackStateCompat.STATE_NONE;
        }
        switch (mExoPlayer.getPlaybackState()) {
            case Player.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case Player.STATE_READY:
                return mExoPlayer.getPlayWhenReady()
                        ? PlaybackStateCompat.STATE_PLAYING
                        : PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_PAUSED;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    /**
     * @return boolean that indicates that this is ready to be used.
     */
    @Override
    public boolean isConnected() {
        return true;
    }

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
        //return (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    /**
     * @return pos if currently playing an item
     */
    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
    }

    /**
     * Queries the underlying stream and update the internal last known stream position.
     */
    @Override
    public void updateLastKnownStreamPosition() {
        // Nothing to do. Position maintained by ExoPlayer.
    }

    @Override
    //public void play(MediaSessionCompat.QueueItem item) {
    public void play(Object item) {
        mPlayOnFocusGain = true;

        boolean mediaHasChanged = false;

        if (item instanceof MediaSessionCompat.QueueItem) {
            String mediaId = ((MediaSessionCompat.QueueItem) item).getDescription().getMediaId();
            mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
            if (mediaHasChanged) {
                mCurrentMediaId = mediaId;
            }
        }

        if (item instanceof String) {
            mediaHasChanged = true;
        }

        if (mediaHasChanged || mExoPlayer == null) {
            releaseResources(false); // release everything except the player

            final Context context = KLApplication.getInstance().getContext();

            //MediaMetadataCompat track =
            //        mMusicProvider.getMusic(
            //                MediaIDHelper.extractMusicIDFromMediaID(
            //                        item.getDescription().getMediaId()));

            //String source = track.getString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE);
            String source = (String) item;// TODO need to separate local media or streaming or podcast contents later
            if (source != null) {
                source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
            }

            if (mExoPlayer == null) {
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(context),
                        new DefaultTrackSelector(),
                        new DefaultLoadControl());
                mExoPlayer.addListener(mEventListener);
            }

            // Android "O" makes much greater use of AudioAttributes, especially
            // with regards to AudioFocus. All of UAMP's tracks are music, but
            // if your content includes spoken word such as audiobooks or podcasts
            // then the content type should be set to CONTENT_TYPE_SPEECH for those
            // tracks.
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes);

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(
                            context, Util.getUserAgent(context, "uamp"), null);
            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            // The MediaSource represents the media to be played.
            ExtractorMediaSource.Factory extractorMediaFactory =
                    new ExtractorMediaSource.Factory(dataSourceFactory);
            extractorMediaFactory.setExtractorsFactory(extractorsFactory);
            MediaSource mediaSource =
                    extractorMediaFactory.createMediaSource(Uri.parse(source));

            // Prepares media to play (happens on background thread) and triggers
            // {@code onPlayerStateChanged} callback when the stream is ready to play.
            mExoPlayer.prepare(mediaSource);

            // If we are streaming from the internet, we want to hold a
            // Wifi lock, which prevents the Wifi radio from going to
            // sleep while the song is playing.
            mWifiLock.acquire();
        }

        configurePlayerState();
    }

    @Override
    public void pause() {
        // Pause player
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }

        // While paused, retain the player instance, but give up audio focus.
        releaseResources(false);
        //unregisterAudioNoisyReceiver();
    }

    @Override
    public void seekTo(long position) {
        Logger.getLogger().e("[INF] seekTo called with "+ position);
        if (mExoPlayer != null) {
            //registerAudioNoisyReceiver();
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void releaseResources(boolean releasePlayer) {
        if (releasePlayer && mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;

            mExoPlayerNullIsStopped = true;

            mPlayOnFocusGain = false;
        }

        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void checkPlayerBeforeUse() {
        if (mExoPlayer == null) {
            throw new IllegalStateException("ERR: The Player must be initialized before using");
        }
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the ExoPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {
        Logger.getLogger().e("[INF] configure player state, play on focus gain="+mPlayOnFocusGain);
        /*
        Logger.getLogger().e("[INF] configurePlayerState. mCurrentAudioFocusState=", mCurrentAudioFocusState);
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause();
        } else {
            registerAudioNoisyReceiver();

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mExoPlayer.setVolume(VOLUME_DUCK);
            } else {
                mExoPlayer.setVolume(VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                mExoPlayer.setPlayWhenReady(true);
                mPlayOnFocusGain = false;
            }
        }
        */

        // If we were playing when we lost focus, we need to resume playing.
        if (mPlayOnFocusGain) {
            mExoPlayer.setPlayWhenReady(true);
            mPlayOnFocusGain = false;
        }
    }
}
