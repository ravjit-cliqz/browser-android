package com.cliqz.browser.controlcenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.cliqz.browser.R;
import com.cliqz.browser.telemetry.TelemetryKeys;

import java.util.List;
import java.util.Locale;

import acr.browser.lightning.view.TrampolineConstants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Ravjit Uppal
 * @author Stefano Pacifici
 */
public class AntiTrackingFragment extends ControlCenterFragment
        implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = AntiTrackingFragment.class.getSimpleName();

    private static final String antiTrackingHelpUrlDe = "https://cliqz.com/whycliqz/anti-tracking";
    private static final String antiTrackingHelpUrlEn = "https://cliqz.com/en/whycliqz/anti-tracking";

    @Bind(R.id.counter)
    TextView counter;

    @Bind(R.id.trackers_list)
    RecyclerView trackersList;

    @Bind(R.id.button_ok)
    AppCompatButton helpButton;

    @Bind(R.id.companies_header)
    TextView companiesHeader;

    @Bind(R.id.counter_header)
    TextView counterHeader;

    @Bind(R.id.upperLine)
    View upperLine;

    @Bind(R.id.lowerLine)
    View lowerLine;

    @Bind(R.id.anti_tracking_table)
    View antitrackingTable;

    @Bind(R.id.attrack_enable)
    Switch enableAttrack;

    @Bind(R.id.attrack_icon)
    AppCompatImageView attrackIcon;

    @Bind(R.id.antitracking_header)
    TextView attrackHeader;

    @Bind(R.id.trackers_blocked)
    TextView trackersBlocked;

    private TrackersListAdapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    protected View onCreateThemedView(LayoutInflater inflater, @Nullable ViewGroup container,
                                      @Nullable Bundle savedInstanceState) {
        mAdapter = new TrackersListAdapter(getContext(), controlCenterCallback, telemetryCallback,
                mIsIncognito);
        final View view = inflater.inflate(R.layout.anti_tracking_layout, container, false);
        ButterKnife.bind(this, view);
        view.setAlpha(0.97f);
        trackersList.setLayoutManager(new LinearLayoutManager(getContext()));
        trackersList.setAdapter(mAdapter);
        final boolean isEnabled = !controlCenterCallback.isUrlWhiteListed(mUrl);
        enableAttrack.setChecked(isEnabled);
        enableAttrack.setText(Uri.parse(mUrl).getHost());
        enableAttrack.setOnCheckedChangeListener(this);
        if (!controlCenterCallback.isAntiTrackingEnabled()) {
            setGenerallyDisabled();
        } else {
            setEnabled(isEnabled);
        }
        return view;
    }

    @OnCheckedChanged(R.id.attrack_enable)
    void onChecked(boolean isEnabled) {
        if (mUrl.contains(TrampolineConstants.TRAMPOLINE_COMMAND_PARAM_NAME)) {
            return;
        }
        setEnabled(isEnabled);
        final String domain = Uri.parse(mUrl).getHost();
        if (isEnabled) {
            controlCenterCallback.removeDomainToWhitelist(domain);
        } else {
            controlCenterCallback.addDomainToWhitelist(domain);
        }
        telemetryCallback.sendCCToggleSignal(isEnabled, TelemetryKeys.ATTRACK);
    }

    private void setGenerallyDisabled() {
        setEnabled(false);
        attrackHeader.setText(getString(R.string.attrack_disabled_header));
        counter.setVisibility(View.GONE);
        trackersBlocked.setText(getString(R.string.attrack_disabled));
        enableAttrack.setVisibility(View.GONE);
        antitrackingTable.setVisibility(View.GONE);
        helpButton.setText(getString(R.string.activate));
    }

    @Override
    protected void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        mAdapter.setEnabled(isEnabled);
        mAdapter.notifyDataSetChanged();
        if (isEnabled) {
            attrackHeader.setText(getString(R.string.antitracking_header));
            trackersBlocked.setText(getString(R.string.antitracking_datapoints));
        } else {
            attrackHeader.setText(getString(R.string.antitracking_header_disabled));
            trackersBlocked.setText(getString(R.string.antitracking_datapoints_disabled));
        }
        controlCenterCallback.updateControlCenterIcon(isEnabled);
    }

    private void setTableVisibility(List<TrackerDetailsModel> details) {
        if (!controlCenterCallback.isAntiTrackingEnabled()) {
            antitrackingTable.setVisibility(View.GONE);
            trackersList.setVisibility(View.GONE);
            return;
        }
        if (details != null && antitrackingTable != null) {
            final int visibility = !details.isEmpty() ? View.VISIBLE : View.GONE;
            antitrackingTable.setVisibility(visibility);
        }
    }

    @OnClick(R.id.button_ok)
    void onOkClicked() {
        controlCenterCallback.closeControlCenter();
        controlCenterCallback.enableAntiTracking();
    }

    @OnClick(R.id.learn_more)
    void onLearnMoreClicked() {
        final String helpUrl = Locale.getDefault().getLanguage().equals("de") ?
                antiTrackingHelpUrlDe : antiTrackingHelpUrlEn;
        controlCenterCallback.openLink(helpUrl);
        controlCenterCallback.closeControlCenter();
        telemetryCallback.sendLearnMoreClickSignal(TelemetryKeys.ATTRACK);
    }

    @Override
    public void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {
        // TODO This class shouldn't implement this method. Remove it.
    }

    @Override
    public void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackCount) {
        if (getView() == null) {
            return;
        }
        counter.setText(String.format(Locale.getDefault(), "%d", trackCount));
        mAdapter.updateList(trackerDetails);
        setTableVisibility(trackerDetails);

        enableAttrack.setText(Uri.parse(mUrl).getHost());
    }

    @Override
    public int getTitle() {
        return R.string.control_center_header_antitracking;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mUrl.contains(TrampolineConstants.TRAMPOLINE_COMMAND_PARAM_NAME)) {
            return;
        }
        setEnabled(isChecked);
        final String domain = Uri.parse(mUrl).getHost();
        if (isChecked) {
            controlCenterCallback.removeDomainToWhitelist(domain);
        } else {
            controlCenterCallback.addDomainToWhitelist(domain);
        }
        telemetryCallback.sendCCToggleSignal(isChecked, TelemetryKeys.ATTRACK);
        controlCenterCallback.reloadCurrentPage();
        if (!isChecked) {
            controlCenterCallback.closeControlCenter();
        }
    }
}
