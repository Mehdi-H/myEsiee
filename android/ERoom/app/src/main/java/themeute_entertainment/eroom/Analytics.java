package themeute_entertainment.eroom;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class Analytics extends Application
{
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    public void onCreate()
    {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(5);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

        tracker = analytics.newTracker(R.xml.analytics_config);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);
    }
}