package com.thebradfo.flickrbrowser;

import android.content.SharedPreferences;

import com.thebradfo.flickrbrowser.di.DaggerAppComponent;
import com.thebradfo.flickrbrowser.settings.ThemeHelper;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

import static com.thebradfo.flickrbrowser.SharedPrefsHelperKt.getFlickrBrowserPrefs;

/**
 * The main Application subclass used to initialize any system-level setup.
 */
public class FlickrBrowserApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        RxJavaPlugins.setErrorHandler(throwable -> Timber.w(throwable, "Handled uncaught stream exception"));

        final SharedPreferences sharedPrefs = getFlickrBrowserPrefs(this);

        // set the daynight theme based upon the shared preferences stored flag.
        ThemeHelper.setDayNightTheme(sharedPrefs.getBoolean(ThemeHelper.DARK_MODE_PREF_NAME, false));
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
