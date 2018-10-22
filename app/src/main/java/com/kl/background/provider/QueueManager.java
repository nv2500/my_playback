package com.kl.background.provider;

import android.content.res.Resources;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

public class QueueManager {

    private final Resources mResources;
    private final MetadataUpdateListener mListener;
    private final List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    public QueueManager(//@NonNull MusicProvider musicProvider,
                        @NonNull Resources resources,
                        @NonNull MetadataUpdateListener listener) {
        //this.mMusicProvider = musicProvider;
        mListener = listener;
        mResources = resources;

        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
    }

    public interface MetadataUpdateListener {
        //void onMetadataChanged(MediaMetadataCompat metadata);
        //void onMetadataRetrieveError();
        void onCurrentQueueIndexUpdated(int queueIndex);
        //void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue);
    }

    public MediaSessionCompat.QueueItem getCurrentMusic() {
        //if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
        //    return null;
        //}
        return mPlayingQueue.get(mCurrentIndex);
    }
}
