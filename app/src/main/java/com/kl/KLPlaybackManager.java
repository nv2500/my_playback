package com.kl;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kl.background.provider.QueueManager;
import com.kl.playback.LocalPlayback;
import com.kl.playback.Playback;
import com.kl.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

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

    private MediaSessionCallback mMediaSessionCallback;

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

        mMediaSessionCallback = new MediaSessionCallback();
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
            // If skipping was not possible, we stop and release the resources:
            handleStopRequest(null);
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


    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    private void playAudio(String mediaUrl) {
        //MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat(
        //        "mediaId", "title", "subtitle",
        //        "description", null, null, null, null);
//        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(null, 10);
//        mPlayback.play(queueItem);

        // mPlayback.play(null);

        Context context = KLApplication.getInstance().getAppContext();

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"), bandwidthMeter);
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);

        ExtractorMediaSource mediaSource = factory.createMediaSource(Uri.parse(mediaUrl));

        ((LocalPlayback)mPlayback).getExoPlayer().prepare(mediaSource);

        // play audio
        ((LocalPlayback)mPlayback).getExoPlayer().setPlayWhenReady(true);

        // pause audio
        // player.setPlayWhenReady(false);
    }
    private void testingMediaResource(Context context) {
        long startTime = System.currentTimeMillis();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        android.database.Cursor cursor = context.getContentResolver().query(
                songUri,
                projection,
                selection,
                null,
                null);


        String mediaData = "";
        if (cursor != null) {
            List<String> songs = new ArrayList<String>();

            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            while (cursor.moveToNext()) {
                // new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));

                long currentId = cursor.getLong(songId);
                String currentTitle = cursor.getString(songTitle);

                String temp = cursor.getString(0) + "||" + cursor.getString(1) + "||" + cursor.getString(2) + "||" + cursor.getString(3) + "||" + cursor.getString(4) + "||" + cursor.getString(5);
                songs.add(temp);
                Logger.getLogger().e("[INF] ??? "+temp);

                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Logger.getLogger().e("[INF] data= "+data);

                mediaData = data;
            }

            cursor.close();
        }
        Logger.getLogger().e("[INF] query music data cost: "+(System.currentTimeMillis() - startTime)+" ms");

        if (!mPlayback.isPlaying() && mediaData != null) {
            //playAudio("/storage/emulated/0/NCT/FallInLove-HoangThuyLinhKimmese-5707418.mp3");
            playAudio(mediaData);
        }
    }

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

         testingMediaResource(KLApplication.getInstance().getAppContext());
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

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            Logger.getLogger().e("[INF] play");
            if (mQueueManager.getCurrentMusic() == null) {
                // mQueueManager.setRandomQueue();
            }
            handlePlayRequest();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            Logger.getLogger().e("[INF] OnSkipToQueueItem:" + queueId);
            //mQueueManager.setCurrentQueueItem(queueId);
            //mQueueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long position) {
            Logger.getLogger().e("[INF] onSeekTo:"+ position);
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Logger.getLogger().e("[INF] playFromMediaId mediaId:"+ mediaId+ "  extras="+ extras);
            //mQueueManager.setQueueFromMusic(mediaId);
            handlePlayRequest();
        }

        @Override
        public void onPause() {
            Logger.getLogger().e("[INF] pause. current state=" + mPlayback.getState());
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            Logger.getLogger().e("[INF] stop. current state=" + mPlayback.getState());
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            Logger.getLogger().e("[INF] skipToNext");
            //if (mQueueManager.skipQueuePosition(1)) {
            //    handlePlayRequest();
            //} else {
            //    handleStopRequest("Cannot skip");
            //}
            //mQueueManager.updateMetadata();
        }

        @Override
        public void onSkipToPrevious() {
            //if (mQueueManager.skipQueuePosition(-1)) {
            //    handlePlayRequest();
            //} else {
            //    handleStopRequest("Cannot skip");
            //}
            //mQueueManager.updateMetadata();
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            /*
            if (CUSTOM_ACTION_THUMBS_UP.equals(action)) {
                Logger.getLogger().e("[INF] onCustomAction: favorite for current track");
                MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
                if (currentMusic != null) {
                    String mediaId = currentMusic.getDescription().getMediaId();
                    if (mediaId != null) {
                        String musicId = MediaIDHelper.extractMusicIDFromMediaID(mediaId);
                        mMusicProvider.setFavorite(musicId, !mMusicProvider.isFavorite(musicId));
                    }
                }
                // playback state needs to be updated because the "Favorite" icon on the
                // custom action will change to reflect the new favorite state.
                updatePlaybackState(null);
            } else {
                Logger.getLogger().e("[INF] Unsupported action: ", action);
            }
            */
        }

        /**
         * Handle free and contextual searches.
         * <p/>
         * All voice searches on Android Auto are sent to this method through a connected
         * {@link android.support.v4.media.session.MediaControllerCompat}.
         * <p/>
         * Threads and async handling:
         * Search, as a potentially slow operation, should run in another thread.
         * <p/>
         * Since this method runs on the main thread, most apps with non-trivial metadata
         * should defer the actual search to another thread (for example, by using
         * an {@link AsyncTask} as we do here).
         **/
        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {
            Logger.getLogger().e("[INF] playFromSearch  query="+ query+ " extras="+ extras);

            mPlayback.setState(PlaybackStateCompat.STATE_CONNECTING);
            /*
            mMusicProvider.retrieveMediaAsync(new MusicProvider.Callback() {
                @Override
                public void onMusicCatalogReady(boolean success) {
                    if (!success) {
                        updatePlaybackState("Could not load catalog");
                    }

                    boolean successSearch = mQueueManager.setQueueFromSearch(query, extras);
                    if (successSearch) {
                        handlePlayRequest();
                        mQueueManager.updateMetadata();
                    } else {
                        updatePlaybackState("Could not find music");
                    }
                }
            });
            */
        }
    }
}
