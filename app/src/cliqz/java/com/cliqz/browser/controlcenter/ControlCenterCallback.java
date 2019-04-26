package com.cliqz.browser.controlcenter;

import android.support.annotation.NonNull;

public interface ControlCenterCallback {

    void enableAdBlocking();

    // TODO Can be a adblockcallback
    void toggleAdBlock(@NonNull String url, boolean domain);

    boolean isAdBlockEnabled();

    boolean isAntiTrackingEnabled();

    // TODO Can be a adblockcallback
    boolean isUrlBlackListed(@NonNull String url);

    boolean isUrlWhiteListed(@NonNull String url);

    // TODO Can be a antitrackingcallback
    void addDomainToWhitelist(String domain);

    // TODO Can be a antitrackingcallback
    void removeDomainToWhitelist(String domain);

    void enableAntiTracking();

    void reloadCurrentPage();

    void closeControlCenter();

    void updateControlCenterIcon(boolean isEnabled);

    void openLink(@NonNull String url);

    void openLink(@NonNull String url, boolean isIncognito);

    interface TelemetryCallback {
        void sendCCTabSignal(String currentPage);

        void sendCCCompanyInfoSignal(int index, String view);

        void sendCCToggleSignal(boolean isChecked, String view);

        void sendLearnMoreClickSignal(String view);

        void sendCCOkSignal(String okOrActivate, String view);
    }
}
