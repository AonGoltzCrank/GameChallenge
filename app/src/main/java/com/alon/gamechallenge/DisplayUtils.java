package com.alon.gamechallenge;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * A Utility class.
 */

class DisplayUtils {


    static final String NAV_BAR_PORTRAIT_HEIGHT = "navigation_bar_height";

    private static DisplayUtils mInstance;

    private WindowManager mWindowMgr;

    private DisplayUtils(Activity caller) {
        mInstance = this;
        mWindowMgr = caller.getWindowManager();
    }

    static DisplayUtils getInstance(Activity caller) {
        return mInstance == null ? new DisplayUtils(caller) : mInstance;
    }

    /**
     * @param realMetricsFlag
     *         - should use {@link android.view.Display#getRealMetrics(DisplayMetrics)} or {@link android.view.Display#getMetrics(DisplayMetrics)}.
     *
     * @return {@link DisplayMetrics} according to realMetricsFlag.
     */
    DisplayMetrics getMetrics(boolean realMetricsFlag) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (realMetricsFlag)
            mWindowMgr.getDefaultDisplay().getRealMetrics(metrics);
        else
            mWindowMgr.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * @param caller
     *         - the caller activity, used to get the resources.
     *
     * @return whether or not the phone has navigation bar enabled or not.
     */
    private boolean hasSoftButtons(Activity caller) {
        Resources resources = caller.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        //Note: doesn't work on emulators.
        return id > 0 && resources.getBoolean(id);
    }

    /**
     * @param caller-
     *         the caller activity, used to get the resources.
     * @param navBarOrientration
     *         - should we find the navBarOrientation by width, height, on landscape mode or portrait mode.
     *
     * @return the height of the navigation bar assuming there is one, 0 if there isn't.
     */
    float getSoftButtonsHeight(Activity caller, String navBarOrientration) {
        if (hasSoftButtons(caller)) {
            Resources resources = caller.getResources();
            int resourceId = resources.getIdentifier(navBarOrientration, "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }
}
