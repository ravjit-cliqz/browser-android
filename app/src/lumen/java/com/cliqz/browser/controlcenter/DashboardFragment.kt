package com.cliqz.browser.controlcenter

import acr.browser.lightning.preference.PreferenceManager
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cliqz.browser.R
import com.cliqz.browser.app.BrowserApp
import com.cliqz.browser.extensions.color
import com.cliqz.browser.extensions.tintVectorDrawable
import com.cliqz.browser.main.Messages
import com.cliqz.browser.purchases.PurchasesManager
import com.cliqz.jsengine.Insights
import com.cliqz.jsengine.ReadableMapUtils
import com.cliqz.nove.Bus
import com.cliqz.nove.Subscribe
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeMap
import kotlinx.android.synthetic.lumen.bond_dashboard_fragment.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import javax.inject.Inject

class DashboardFragment : Fragment() {

    private val DATA_SAVED = "dataSaved"
    private val ADS_BLOCKED = "adsBlocked"
    private val TRACKERS_DETECTED = "trackersDetected"
    private val PAGES_CHECKED = "pages"

    private var isDailyView = false

    @Inject
    internal lateinit var bus: Bus

    @Inject
    internal lateinit var insights: Insights

    @Inject
    internal lateinit var purchasesManager: PurchasesManager

    @Inject
    internal lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDailyView = arguments?.getBoolean(ControlCenterPagerAdapter.IS_TODAY) ?: true
        BrowserApp.getActivityComponent(activity)?.inject(this)
        bus.register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bond_dashboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeDashboardState(purchasesManager.isDashboardEnabled() &&
                preferenceManager.isAttrackEnabled && preferenceManager.adBlockEnabled)
        reset.setOnClickListener {
            if (!preferenceManager.isAttrackEnabled && !preferenceManager.adBlockEnabled) {
                return@setOnClickListener
            }
            android.app.AlertDialog.Builder(it.context)
                    .setTitle(R.string.bond_dashboard_clear_dialog_title)
                    .setMessage(R.string.bond_dashboard_clear_dialog_message)
                    .setPositiveButton(R.string.button_ok) { _, _ ->
                        bus.post(Messages.ClearDashboardData())
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
        updateUI()
    }

    fun updateUI() {
        val periodType = if (isDailyView) "day" else "week"
        insights.getInsightsData({
            view?.post {
                if (it.hasKey("result")) {
                    updateViews(it.getMap("result"))
                } else {
                    updateViews(getSavedDashboardState())
                }
            }
        }, periodType)
    }

    fun refreshUIComponent(optionValue: Boolean) {
        changeDashboardState(optionValue)
    }

    private fun updateViews(data: ReadableMap?) {
        if (data == null) return

        saveDashboardState(data)

        val dataSaved = ValuesFormatterUtil.formatBytesCount(ReadableMapUtils.getSafeInt(data, DATA_SAVED))
        val adsBlocked = ValuesFormatterUtil.formatBlockCount(ReadableMapUtils.getSafeInt(data, ADS_BLOCKED))
        val trackersDetected = ValuesFormatterUtil.formatBlockCount(ReadableMapUtils.getSafeInt(data, TRACKERS_DETECTED))
        val pagesVisited = ValuesFormatterUtil.formatBlockCount(ReadableMapUtils.getSafeInt(data, PAGES_CHECKED))

        ads_blocked.text = adsBlocked.value
        trackers_detected.text = trackersDetected.value
        data_saved.text = dataSaved.value
        phishing_checked.text = pagesVisited.value

        data_saved_icon.setImageResource(when (dataSaved.unit) {
            R.string.bond_dashboard_units_kb -> R.drawable.ic_kb_data_saved_on
            R.string.bond_dashboard_units_mb -> R.drawable.ic_mb_data_saved_on
            R.string.bond_dashboard_units_gb -> R.drawable.ic_gb_data_saved_on
            else -> throw IllegalArgumentException("Wrong unit for 'data saved' data")
        })
    }

    private fun changeDashboardState(isEnabled: Boolean) {
        if (isEnabled) {
            ads_blocked_icon.clearColorFilter()
            trackers_detected_icon.clearColorFilter()
            data_saved_icon.clearColorFilter()
            phishing_checked_icon.clearColorFilter()
            context?.apply {
                ads_blocked.setTextColor(Color.WHITE)
                trackers_detected.setTextColor(Color.WHITE)
                data_saved.setTextColor(Color.WHITE)
                phishing_checked.setTextColor(Color.WHITE)

                ads_blocked_text.setTextColor(Color.WHITE)
                trackers_detected_text.setTextColor(Color.WHITE)
                data_saved_text.setTextColor(Color.WHITE)
                phishing_checked_text.setTextColor(Color.WHITE)

                reset.setTextColor(color(R.color.lumen_color_blue_primary))

                vertical_line.setBackgroundColor(color(R.color.lumen_color_blue_primary_opaque))
                horizontal_line.setBackgroundColor(color(R.color.lumen_color_blue_primary_opaque))
            }
        } else {
            ads_blocked_icon.tintVectorDrawable(R.color.lumen_color_grey_text)
            trackers_detected_icon.tintVectorDrawable(R.color.lumen_color_grey_text)
            data_saved_icon.tintVectorDrawable(R.color.lumen_color_grey_text)
            phishing_checked_icon.tintVectorDrawable(R.color.lumen_color_grey_text)
            context?.apply {
                ads_blocked.setTextColor(color(R.color.lumen_color_grey_text))
                trackers_detected.setTextColor(color(R.color.lumen_color_grey_text))
                data_saved.setTextColor(color(R.color.lumen_color_grey_text))
                phishing_checked.setTextColor(color(R.color.lumen_color_grey_text))

                ads_blocked_text.setTextColor(color(R.color.lumen_color_grey_text))
                trackers_detected_text.setTextColor(color(R.color.lumen_color_grey_text))
                data_saved_text.setTextColor(color(R.color.lumen_color_grey_text))
                phishing_checked_text.setTextColor(color(R.color.lumen_color_grey_text))

                reset.setTextColor(color(R.color.lumen_color_grey_text))

                vertical_line.setBackgroundColor(color(R.color.lumen_color_grey_text))
                horizontal_line.setBackgroundColor(color(R.color.lumen_color_grey_text))
            }
        }
    }

    @Subscribe
    internal fun onPurchaseCompleted(purchaseCompleted: Messages.PurchaseCompleted) {
        if (purchasesManager.purchase.isDashboardEnabled) {
            bus.post(Messages.EnableAdBlock())
            bus.post(Messages.EnableAttrack())
            bus.post(Messages.OnDashboardStateChange())
            changeDashboardState(true)
        }
    }

    private fun saveDashboardState(data: ReadableMap) {
        val dataSaved = ReadableMapUtils.getSafeInt(data, "dataSaved")
        val adsBlocked = ReadableMapUtils.getSafeInt(data, "adsBlocked")
        val trackersDetected = ReadableMapUtils.getSafeInt(data, "trackersDetected")
        val pagesVisited = ReadableMapUtils.getSafeInt(data, "pages")
        val dashboardState = DashboardState(adsBlocked, trackersDetected, dataSaved, pagesVisited)
        val bos = ByteArrayOutputStream()
        ObjectOutputStream(bos).use {
            it.writeObject(dashboardState)
            val base64 = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
            preferenceManager.dashboardData = base64
        }
    }

    private fun getSavedDashboardState() : ReadableMap {
        if (preferenceManager.dashboardData == null) {
            return WritableNativeMap()
        }
        val bis = ByteArrayInputStream(Base64.decode(preferenceManager.dashboardData, Base64.DEFAULT))
        ObjectInputStream(bis).use {
            val dashboardState = it.readObject() as DashboardState
            val map = WritableNativeMap()
            map.putInt(DATA_SAVED, dashboardState.dataSaved)
            map.putInt(ADS_BLOCKED, dashboardState.adsBlocked)
            map.putInt(TRACKERS_DETECTED, dashboardState.trackersDetected)
            map.putInt(PAGES_CHECKED, dashboardState.pagesVisited)
            return map
        }
    }

}