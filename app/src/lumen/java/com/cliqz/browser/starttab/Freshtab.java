package com.cliqz.browser.starttab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cliqz.browser.R;

public class Freshtab extends StartTabFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tab_fragment_background));
        return frameLayout;
    }

    @Override
    String getTitle() {
        return "";
    }

    @Override
    int getIconId() {
        return R.drawable.ic_fresh_tab;
    }
}