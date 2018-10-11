package com.kl.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.kl.utils.Logger;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    private View rootView;

    protected abstract String screenName();

    private int rdn_bg_color = getRandomColor();

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onInflate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Logger.getLogger().e(getClass().getSimpleName()+": [INF] onAttach");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onAttachFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onCreateView");

        rootView = inflater.inflate(R.layout.basefragment, container, false);

        TextView tv = rootView.findViewById(R.id.textView);
        tv.setText(screenName());

        rootView.setBackgroundColor(rdn_bg_color);

        //return super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    private int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onViewCreated");

//        Random rnd = new Random();
//        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        rootView.setBackgroundColor(color);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onActivityCreated");

        if (savedInstanceState != null) {
            int savedValue = savedInstanceState.getInt("RDN_BG");
            Logger.getLogger().i("[INF] onActivityCreated - saved value="+savedValue);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onViewStateRestored");

        if (savedInstanceState != null) {
            int savedValue = savedInstanceState.getInt("RDN_BG");
            Logger.getLogger().i("[INF] onViewStateRestored - saved value="+savedValue);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.getLogger().i(getClass().getSimpleName()+": [INF] onPause");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("RDN_BG", rdn_bg_color);

        super.onSaveInstanceState(outState);

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onSaveInstanceState");
    }

    @Override
    public void onStop() {
        super.onStop();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Logger.getLogger().d(getClass().getSimpleName()+": [INF] onDetach");
    }
}
