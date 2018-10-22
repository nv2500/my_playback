package com.kl;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.kl.background.provider.QueueManager;
import com.kl.playback.LocalPlayback;
import com.kl.playback.Playback;
import com.kl.utils.Logger;

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
    private PlaybackServiceCallback mServiceCallback;
    private QueueManager mQueueManager;

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

    private KLPlaybackManager() {
        // force empty constructor here
        // restrict instantiation
    }
    public KLPlaybackManager(Context context,
                             QueueManager queueManager,
                             PlaybackServiceCallback serviceCallback) {
        mPlayback = new LocalPlayback(context);
        mPlayback.setCallback(this);

        mQueueManager = queueManager;

        mServiceCallback = serviceCallback;
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
        // The media player finished playing the current song, so we go ahead
        // and start the next.
//        if (mQueueManager.skipQueuePosition(1)) {
//            handlePlayRequest();
//            mQueueManager.updateMetadata();
//        } else {
//            // If skipping was not possible, we stop and release the resources:
//            handleStopRequest(null);
//        }
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
        updatePlaybackState(null);
    }

    /**
     * @param error to be added to the PlaybackState
     */
    @Override
    public void onError(String error) {
        Logger.getLogger().e(error);
        updatePlaybackState(error);
    }

    /**
     * @param mediaId being currently played
     */
    @Override
    public void setCurrentMediaId(String mediaId) {

    }
    // for Playback.Callback - E


//    public void playAudio(String radioUrl) {
//        //MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat(
//        //        "mediaId", "title", "subtitle",
//        //        "description", null, null, null, null);
////        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(null, 10);
////        mPlayback.play(queueItem);
//
//        mPlayback.play(null);
//
//        /*
//        Context context = KLApplication.getInstance().getContext();
//
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"), bandwidthMeter);
//        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);
//
//        ExtractorMediaSource mediaSource = factory.createMediaSource(Uri.parse(radioUrl));
//
//        mExoPlayer.prepare(mediaSource);
//
//        // play audio
//        mExoPlayer.setPlayWhenReady(true);
//
//        // pause audio
//        // player.setPlayWhenReady(false);
//        */
//    }

    public void releaseResources(boolean releasePlayer) {
        mPlayback.releaseResources(releasePlayer);
    }

    /**
     * Handle a request to play music
     */
    public void handlePlayRequest() {
        Logger.getLogger().e("[INF] handlePlayRequest: mState=" + mPlayback.getState());
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentMusic);
        }
    }

    /**
     * Handle a request to pause music
     */
    public void handlePauseRequest() {
        Logger.getLogger().e("[INF] handlePauseRequest: mState=" + mPlayback.getState());
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    /**
     * Handle a request to stop music
     *
     * @param withError Error message in case the stop has an unexpected cause. The error
     *                  message will be set in the PlaybackState and will be visible to
     *                  MediaController clients.
     */
    public void handleStopRequest(String withError) {
        Logger.getLogger().e("[INF] handleStopRequest: mState=" + mPlayback.getState() + " error="+ withError);
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
    }

    /**
     * Update the current media player state, optionally showing an error message.
     *
     * @param error if not null, error message to present to the user.
     */
    public void updatePlaybackState(String error) {
        Logger.getLogger().e("[INF] updatePlaybackState, playback state=" + mPlayback.getState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        setCustomAction(stateBuilder);
        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            //stateBuilder.setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR, error);
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

//        // Set the activeQueueItemId if the current index is valid.
//        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
//        if (currentMusic != null) {
//            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
//        }

        if (mServiceCallback != null) {
            mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

            if (state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_PAUSED) {
                mServiceCallback.onNotificationRequired();
            }
        }
    }
    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }
    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder) {
//        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
//        if (currentMusic == null) {
//            return;
//        }
//        // Set appropriate "Favorite" icon on Custom action:
//        String mediaId = currentMusic.getDescription().getMediaId();
//        if (mediaId == null) {
//            return;
//        }
//        String musicId = MediaIDHelper.extractMusicIDFromMediaID(mediaId);
//        int favoriteIcon = mMusicProvider.isFavorite(musicId) ?
//                R.drawable.ic_star_on : R.drawable.ic_star_off;
//        LogHelper.d(TAG, "updatePlaybackState, setting Favorite custom action of music ",
//                musicId, " current favorite=", mMusicProvider.isFavorite(musicId));
//        Bundle customActionExtras = new Bundle();
//        WearHelper.setShowCustomActionOnWear(customActionExtras, true);
//        stateBuilder.addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
//                CUSTOM_ACTION_THUMBS_UP, mResources.getString(R.string.favorite), favoriteIcon)
//                .setExtras(customActionExtras)
//                .build());
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}
