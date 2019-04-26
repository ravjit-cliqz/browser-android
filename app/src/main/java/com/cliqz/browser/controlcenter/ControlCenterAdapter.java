package com.cliqz.browser.controlcenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cliqz.browser.app.BrowserApp;

import java.util.List;

/**
 * @author Ravjit Uppal
 * @author Stefano Pacifici
 */
class ControlCenterAdapter extends FragmentPagerAdapter {

    private final List<ControlCenterFragment> fragments;

    ControlCenterAdapter(FragmentManager fm, List<ControlCenterFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return BrowserApp.getAppContext().getResources().getString(fragments.get(position).getTitle());
    }

    void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {
        for (ControlCenterFragment fragment : fragments) {
            fragment.updateAdBlockList(adBlockDetails);
        }
    }

    void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackCount) {
        for (ControlCenterFragment fragment : fragments) {
            fragment.updateTrackerList(trackerDetails, trackCount);
        }
    }

    void setUrl(String url) {
        for (ControlCenterFragment fragment : fragments) {
            fragment.setUrl(url);
        }
    }

    void setIsIncognito(boolean isIncognito) {
        for (ControlCenterFragment fragment : fragments) {
            fragment.setIsIncognito(isIncognito);
        }
    }

}
