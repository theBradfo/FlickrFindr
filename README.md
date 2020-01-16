# FlickrFindr
Sample Flickr Browser app

## Static Analysis
- Added Detekt for kotlin static analysis and confirmed 0 failures with default config.
- Ran lint and confirmed only a few neglible errors present.

## Details
- Leveraged the Material Design Components Theme Builder to generate a proper material theme, which supports DayNight. The app has a "dark mode" toggle in-app.
- Using a bottom app bar to experiment with the notched FAB and actionbar menu items.
- Using dagger2 to manage depenencies app-wide.
- Single activity setup with multiple fragments.

## Future work
- Add in androidx navigation components to manage navigation between the fragments.
- Add an androidx ViewModel to better retain state across activity configuration changes.  The reduce these, i've locked the activity into portrait orientation.
- Robolectric and/or Espresso tests for UI testing.
- Support bookmarking and offline image loading of these bookmarked/cached images.
