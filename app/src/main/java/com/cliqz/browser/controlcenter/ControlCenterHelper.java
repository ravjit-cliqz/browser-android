package com.cliqz.browser.controlcenter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ControlCenterHelper {

    private static final String LOGTAG = "ControlCenterHelper";
    private final List<ControlCenterFragment> mFragmentList = new ArrayList<>();
    private ControlCenterContainer mControlCenterContainer;
    private ControlCenterCallback mControlCenterCallback;
    private ControlCenterCallback.TelemetryCallback mTelemetryCallback;
    private FragmentManager fragmentManager;
    private View mContentContainer;

    public ControlCenterHelper(@NonNull ControlCenterContainer controlCenterContainer,
                               @NonNull View contentContainer, ControlCenterCallback controlCenterCallback,
                               ControlCenterCallback.TelemetryCallback telemetryCallback,
                               FragmentManager fragmentManager) {
        this.mControlCenterContainer = controlCenterContainer;
        this.mContentContainer = contentContainer;
        this.mControlCenterCallback = controlCenterCallback;
        this.mTelemetryCallback = telemetryCallback;
        this.fragmentManager = fragmentManager;
    }

    public void init() {
        mControlCenterContainer.init(fragmentManager, mFragmentList);
        mFragmentList.clear();
        ControlCenterFragment antiTrackingFragment = new AntiTrackingFragment();
        ControlCenterFragment antiBlockingFragment = new AdBlockingFragment();
        ControlCenterFragment antiPhishingFragment = new AntiPhishingFragment();
        antiTrackingFragment.setCallbacks(mControlCenterCallback, mTelemetryCallback);
        antiBlockingFragment.setCallbacks(mControlCenterCallback, mTelemetryCallback);
        antiPhishingFragment.setCallbacks(mControlCenterCallback, mTelemetryCallback);
        mFragmentList.add(antiTrackingFragment);
        mFragmentList.add(antiBlockingFragment);
        mFragmentList.add(antiPhishingFragment);
        mControlCenterContainer.setTelemetryCallback(mTelemetryCallback);
    }


    public void toggleControlCenter() {
        if (mControlCenterContainer.getVisibility() == View.VISIBLE) {
            mControlCenterContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            init();
            mControlCenterContainer.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
        }
    }

    public void closeControlCenter() {
        mControlCenterContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    public void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {
        mControlCenterContainer.updateAdBlockList(adBlockDetails);
    }

    public void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackerCount) {
        mControlCenterContainer.updateTrackerList(trackerDetails, trackerCount);
    }

    public void setUrl(String url) {
        mControlCenterContainer.setUrl(url);
    }

    public void setIsIncognito(boolean isIncognito) {
        mControlCenterContainer.setIsIncognito(isIncognito);
    }

}
