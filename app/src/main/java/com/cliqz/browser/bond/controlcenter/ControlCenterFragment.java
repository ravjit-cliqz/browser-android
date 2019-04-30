package com.cliqz.browser.bond.controlcenter;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Copyright © Cliqz 2019
 */
public abstract class ControlCenterFragment extends Fragment {

    public abstract String getTitle(Context context, int position);

    public abstract void updateUI(GeckoBundle data);

    public abstract void refreshUI();

    public abstract void refreshUIComponent(int id, boolean optionValue);
}
