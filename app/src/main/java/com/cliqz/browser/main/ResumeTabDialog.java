package com.cliqz.browser.main;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.cliqz.browser.R;

/**
 * @author Stefano Pacifici
 */
class ResumeTabDialog implements Dialog.OnClickListener, DialogInterface.OnCancelListener {

    private static boolean M_IS_SHOWN = false;
    private MainActivity mMainActivity;

    static boolean isShown() {
        return M_IS_SHOWN;
    }

    public static void show(MainActivity activity) {
        final ResumeTabDialog resumeTabDialog = new ResumeTabDialog();
        M_IS_SHOWN = true;
        resumeTabDialog.mMainActivity = activity;
        new AlertDialog.Builder(activity)
                .setTitle(R.string.resume_tab_dialog_title)
                .setMessage(R.string.resume_tab_dialog_msg)
                .setPositiveButton(R.string.action_resume, resumeTabDialog)
                .setCancelable(true)
                .setNegativeButton(R.string.action_cancel, resumeTabDialog)
                .setOnCancelListener(resumeTabDialog)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                mMainActivity.tabsManager.restoreTabs();
                mMainActivity.tabsManager.resumeAllTabs();
                break;
            case Dialog.BUTTON_NEGATIVE:
                if (mMainActivity.tabsManager.getCurrentTab() != null) {
                    mMainActivity.tabsManager.getCurrentTab().searchBar.showTitleBar();
                }
                break;
            default:
                mMainActivity.tabsManager.clearTabsData();
                break;
        }
        M_IS_SHOWN = false;
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        M_IS_SHOWN = false;
        dialog.dismiss();
    }
}
