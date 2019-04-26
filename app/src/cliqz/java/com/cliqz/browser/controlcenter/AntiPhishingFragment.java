package com.cliqz.browser.controlcenter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cliqz.browser.R;
import com.cliqz.browser.telemetry.TelemetryKeys;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ravjit Uppal
 * @author Stefano Pacifici
 */
public class AntiPhishingFragment extends ControlCenterFragment {

    private static final String antiPhishingHelupUrlDe = "https://cliqz.com/whycliqz/anti-phishing";
    private static final String antiPhishingHelupUrlEn = "https://cliqz.com/en/whycliqz/anti-phishing";

    @Bind(R.id.button_ok)
    AppCompatButton helpButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View onCreateThemedView(LayoutInflater inflater, @Nullable ViewGroup container,
                                      @Nullable Bundle savedInstanceState) {
        final @LayoutRes int layout;
        final Configuration config = getResources().getConfiguration();
        switch (config.orientation) {
            case Configuration.ORIENTATION_UNDEFINED:
                if (config.screenWidthDp > config.screenHeightDp) {
                    layout = R.layout.anti_phishing_land;
                } else {
                    layout = R.layout.anti_phishing;
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                layout = R.layout.anti_phishing_land;
                break;
            default:
                layout = R.layout.anti_phishing;
                break;
        }
        final View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);
        view.setAlpha(0.97f);
        return view;
    }

    @Override
    public void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {

    }

    @Override
    public void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackCount) {

    }

    @OnClick(R.id.button_ok)
    void onHelpClickd() {
        controlCenterCallback.closeControlCenter();
        telemetryCallback.sendCCOkSignal(TelemetryKeys.OK, TelemetryKeys.ATPHISH);
    }

    @Override
    public int getTitle() {
        return R.string.control_center_header_antiphishing;
    }

    @OnClick(R.id.learn_more)
    void onLearnMoreClicked() {
        final String helpUrl = Locale.getDefault().getLanguage().equals("de") ?
                antiPhishingHelupUrlDe : antiPhishingHelupUrlEn;
        controlCenterCallback.openLink(helpUrl);
        controlCenterCallback.closeControlCenter();
        telemetryCallback.sendLearnMoreClickSignal(TelemetryKeys.ATPHISH);
    }
}
