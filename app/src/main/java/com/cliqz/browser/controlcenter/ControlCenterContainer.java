package com.cliqz.browser.controlcenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.cliqz.browser.R;
import com.cliqz.browser.app.BrowserApp;
import com.cliqz.browser.telemetry.TelemetryKeys;

import java.util.List;

/**
 * @author Ravjit Uppal
 */
public class ControlCenterContainer extends RelativeLayout {

    private static String TAG = ControlCenterContainer.class.getSimpleName();

    TabLayout controlCenterHeaders;
    ViewPager controlCenterPager;

    private ControlCenterAdapter controlCenterAdapter;

    ControlCenterCallback.TelemetryCallback telemetryCallback;

    public ControlCenterContainer(Context context) {
        this(context, null);
    }

    public ControlCenterContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlCenterContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.control_center_layout, null );
        addView(view);
    }

    public void setTelemetryCallback(ControlCenterCallback.TelemetryCallback telemetryCallback) {
        this.telemetryCallback = telemetryCallback;
    }

    public void init(FragmentManager fm, List<ControlCenterFragment> controlCenterFragments) {
        controlCenterPager = findViewById(R.id.control_center_pager);
        controlCenterAdapter = new ControlCenterAdapter(fm, controlCenterFragments);
        controlCenterPager.setAdapter(controlCenterAdapter);
        controlCenterHeaders = findViewById(R.id.sec_features);
        controlCenterHeaders.setupWithViewPager(controlCenterPager);
        controlCenterPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                final String currentPage;
                switch (position) {
                    case 0:
                        currentPage = TelemetryKeys.ATTRACK;
                        break;
                    case 1:
                        currentPage = TelemetryKeys.ADBLOCK;
                        break;
                    case 2:
                        currentPage = TelemetryKeys.ATPHISH;
                        break;
                    default:
                        currentPage = TelemetryKeys.ATTRACK;
                }
                if (telemetryCallback != null) {
                    telemetryCallback.sendCCTabSignal(currentPage);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });
    }

    public void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {
        controlCenterAdapter.updateAdBlockList(adBlockDetails);
    }

    public void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackerCount) {
        controlCenterAdapter.updateTrackerList(trackerDetails, trackerCount);
    }

    public void setUrl(String url) {
        controlCenterAdapter.setUrl(url);
    }

    public void setIsIncognito(boolean isIncognito) {
        controlCenterAdapter.setIsIncognito(isIncognito);
    }
}
