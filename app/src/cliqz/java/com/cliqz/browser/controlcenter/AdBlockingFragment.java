package com.cliqz.browser.controlcenter;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ravjit Uppal
 * @author Stefano Pacifici
 */
public class AdBlockingFragment extends ControlCenterFragment implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = AdBlockingFragment.class.getSimpleName();

    private static final String adBlockingHelpUrlDe = "https://cliqz.com/whycliqz/adblocking";
    private static final String adBlockingHelpUrlEn = "https://cliqz.com/en/whycliqz/adblocking";

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

    @Bind(R.id.adblock_enable)
    Switch enableAdBlock;

    @Bind(R.id.adblock_icon)
    AppCompatImageView adBlockIcon;

    @Bind(R.id.adblocking_header)
    TextView adblockHeader;

    @Bind(R.id.ads_blocked)
    TextView adsBlocked;

    private AdBlockListAdapter mAdapter;

    @Override
    protected View onCreateThemedView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAdapter = new AdBlockListAdapter(getContext(), controlCenterCallback, telemetryCallback,
                mIsIncognito);
        final @LayoutRes int layout;
        final Configuration config = getResources().getConfiguration();
        switch (config.orientation) {
            case Configuration.ORIENTATION_UNDEFINED:
                if (config.screenWidthDp > config.screenHeightDp) {
                    layout = R.layout.ad_blocking_layout_land;
                } else {
                    layout = R.layout.ad_blocking_layout;
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                layout = R.layout.ad_blocking_layout_land;
                break;
            default:
                layout = R.layout.ad_blocking_layout;
                break;
        }
        final View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);
        view.setAlpha(0.97f);
        trackersList.setLayoutManager(new LinearLayoutManager(getContext()));
        trackersList.setAdapter(mAdapter);
        final boolean isEnabled = !controlCenterCallback.isUrlBlackListed(mUrl);
        enableAdBlock.setChecked(isEnabled);
        enableAdBlock.setText(Uri.parse(mUrl).getHost());
        enableAdBlock.setOnCheckedChangeListener(this);
        setEnabled(isEnabled);
        if (!controlCenterCallback.isAdBlockEnabled()) {
            setAdBlockGloballyDisabledView();
        }
        return view;
    }

    private void setAdBlockGloballyDisabledView() {
        super.setEnabled(false);
        adblockHeader.setText(getString(R.string.ad_blocker_disabled_header));
        counter.setVisibility(View.GONE);
        adsBlocked.setText(getString(R.string.ad_blocker_disabled));
        enableAdBlock.setVisibility(View.GONE);
        antitrackingTable.setVisibility(View.GONE);
        helpButton.setText(Objects.requireNonNull(getContext()).getString(R.string.activate));
    }

    @Override
    protected void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mAdapter.setEnabled(enabled);
        mAdapter.notifyDataSetChanged();
        final Context context = Objects.requireNonNull(getContext());
        if (enabled) {
            adblockHeader.setText(context.getString(R.string.adblocking_header));
            adsBlocked.setText(context.getString(R.string.adblocking_ads_blocked));
        } else {
            adblockHeader.setText(getContext().getString(R.string.adblocking_header_disabled));
            adsBlocked.setText(getContext().getString(R.string.adblocking_ads_blocked_disabled));
        }
    }

    private int countTotalAds(List<AdBlockDetailsModel> mDetails) {
        int adCount = 0;
        for (AdBlockDetailsModel model : mDetails) {
            adCount += model.adBlockCount;
        }
        return adCount;
    }

    private void setTableVisibility(List<AdBlockDetailsModel> details) {
        if (!controlCenterCallback.isAdBlockEnabled()) {
            antitrackingTable.setVisibility(View.GONE);
            trackersList.setVisibility(View.GONE);
            return;
        }
        if (details != null && antitrackingTable != null) {
            final int visibility = details.size() > 0 ?
                    View.VISIBLE : View.GONE;
            antitrackingTable.setVisibility(visibility);
        }

    }

    @Override
    public int getTitle() {
        return R.string.control_center_header_adblocking;
    }

    @OnClick(R.id.button_ok)
    void onOkClicked() {
        controlCenterCallback.closeControlCenter();
        controlCenterCallback.enableAdBlocking();
    }

    @OnClick(R.id.learn_more)
    void onLearnMoreClicked() {
        final String helpUrl = Locale.getDefault().getLanguage().equals("de") ?
                adBlockingHelpUrlDe : adBlockingHelpUrlEn;
        controlCenterCallback.openLink(helpUrl);
        controlCenterCallback.closeControlCenter();
        telemetryCallback.sendLearnMoreClickSignal(TelemetryKeys.ADBLOCK);
    }

    @Override
    public void updateAdBlockList(List<AdBlockDetailsModel> adBlockDetails) {
        if (getView() == null) return;
        final int adCount = countTotalAds(adBlockDetails);
        counter.setText(String.format(Locale.getDefault(), "%d", adCount));
        mAdapter.updateList(adBlockDetails);
        setTableVisibility(adBlockDetails);
        enableAdBlock.setText(Uri.parse(mUrl).getHost());
    }

    @Override
    public void updateTrackerList(List<TrackerDetailsModel> trackerDetails, int trackCount) {
        // Do nothing
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setEnabled(isChecked);
        controlCenterCallback.toggleAdBlock(mUrl, true);
        telemetryCallback.sendCCToggleSignal(isChecked, TelemetryKeys.ADBLOCK);
        controlCenterCallback.closeControlCenter();
        controlCenterCallback.reloadCurrentPage();
    }

}
