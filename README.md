Daily Dozen Android App
========================

<p align="center"><img src="http://nutritionfactsorg.s3.amazonaws.com/wp-content/uploads/2016/03/21232747/github.jpg" style="width: 600px"></p>

About
-----------

In the years of research required to create the more than a thousand evidence-based videos on [NutritionFacts.org][nutritionfacts.org], Michael Greger, MD, FACLM, has arrived at a list of what he considers the most important foods to include in a healthy daily diet. Yes, greens are good for you, but how much should we try to eat each day?

Dr. Greger’s Daily Dozen details the healthiest foods and how many servings of each we should try to check off every day. He explains his rationale in his book [How Not to Die][book]. All his proceeds from his books, DVDs, and speaking engagements is all donated to charity.


Daily Dozen on Google Play
------------------

<a href="https://play.google.com/store/apps/details?id=org.nutritionfacts.dailydozen" alt="Get it on Google Play" target="_blank"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" height="80"></a>

Contribute
------------

We would love for you to contribute to our source code and to help make the Daily Dozen for Android even better!

Check out our [Contribution Guidelines][contribute] for details on how to get started and our suggested best practices.

Donate
------

To help support [NutritionFacts.org][nutritionfacts.org], click [here][donate]

License
-------

The Daily Dozen Android App is licensed under the GPLv3

Contributors
------------

* [John Slavick][slavick] (Maintainer)
* [Christi Richards][christirichards]
* [Lauren Hacker][laurenhacker]

Special thanks to the volunteer efforts of the creators of the previous app: Chan Kruse, Allan Portera, and Sangeeta Kumar

Updates
-------

### Version 14
- Added CheckBoxes for Vitamin B12 and Vitamin D

### Version 13.1
- Fixed minor bug where notification was not being dismissed unless the app was opened from the notification
- Improvements to the German translation - thanks to contributor [caugner](https://github.com/caugner)

### Version 13
- Can now switch between metric and Imperial units
- Tapping on a date in the calendar or chart view now takes you to that date
- Reverted the MPAndroidChart version as the newer library was causing crashes

### Version 12.2
- Fixed black background issue on Samsung devices
- You can now move the app to your device's SD card

### Version 12
- Improved performance and functionality of the Daily Servings History graph
- Redesigned checkbox layout to improve support for translations and small screens
- Added Spanish translation - thanks to contributor Monica Suarez
- Added French translation - thanks to contributor [vgstef](https://github.com/vgstef)
- Added Dutch translation - thanks to contributor [gfvpelt](https://github.com/gfvpelt)
- Added Bulgarian translation - thanks to contributor [haripetrov](https://github.com/haripetrov)
- Added Greek translation - thanks to contributor [kapcom01](https://github.com/kapcom01)

### Version 11
- Added German translation - thanks to contributor [MovGP0](https://github.com/MovGP0)
- Improved CheckBox functionality - thanks to contributor [notmypassword](https://github.com/notmypassword)
- Fixed swapped images - thanks to contributor [myang310](https://github.com/myang310)
- Fixed bug where last CheckBox for Beverages was hidden on devices with small screens
- Fixed a few minor crashes

### Version 10
- Added links to videos on nutritionfacts.org. These links are for overall food groups like beans and greens, but also for specific foods like chickpeas and spinach.
- Fixed bug where update reminder alarm was not being set when the device restarted.
- Fixed very rare OutOfMemoryError crash when loading the large food images.
- Upgraded to ButterKnife 8.1.0

### Version 9
- Added a Daily Reminder to update your servings
- Fixed bug where every other day was being skipped in the daily servings chart
- Fixed bug where failing to load the image in FoodInfoActivity was crashing the app
- Removed allowBackup="false" from AndroidManifest.xml. This will allow devices running Marshmallow and later to automatically backup to the user's Google Account.
- Upgraded to ButterKnife 8.0.1

### Version 8 - Completely new app
- Entries are now persisted over time and can be graphed and charted
- Added streaks so you can see at a glance how many days you have met your required servings
- History can be backed up to a comma-separated values text file

### 1.0.1 - 1.0.2 (Google Play)
- Misspelling fix

### 1.0.0 (Google Play)
- Initial Release

[nutritionfacts.org]: http://nutritionfacts.org "NutritionFacts.org - The Latest in Nutrition Research"
[contribute]: https://github.com/nutritionfactsorg/daily-dozen-android/blob/master/CONTRIBUTING.md "Contribute to the Daily Dozen Android App"
[donate]: https://nutritionfacts.org/donate "Donate to NutritionFacts.org"
[book]: http://nutritionfacts.org/book "How Not to Die"
[slavick]: http://github.com/slavick "John Slavick on GitHub"
[christirichards]: http://github.com/christirichards "Christi Richards on GitHub"
[laurenhacker]: http://github.com/lahacker "Lauren Hacker on GitHub"