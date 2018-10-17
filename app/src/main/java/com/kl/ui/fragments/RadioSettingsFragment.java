package com.kl.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.kl.ui.BaseFragment;
import com.kl.utils.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RadioSettingsFragment extends BaseFragment {
    @Override
    protected String screenName() {
        return "[RadioSettings] ";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onCreateView");

        rootView = inflater.inflate(R.layout.radio_settings_fragment, container, false);

        return rootView;
    }


}
