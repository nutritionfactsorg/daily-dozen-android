# Download the database file to the current working directory.
# NOTE: This only works on debuggable builds of the app
adb shell "run-as org.nutritionfacts.dailydozen cp /data/data/org.nutritionfacts.dailydozen/databases/dailydozen.db /sdcard/dailydozen.db"
adb pull "/sdcard/dailydozen.db"
adb shell "rm /sdcard/dailydozen.db"

# Append the current date and time to the file. This way we won't overwrite previously pulled database files.
mv dailydozen.db DailyDozenDB_$(date +"%Y%m%d%H%M%S").db

# Open the latest database file
# NOTE: This only works if your computer has a program that reads SQLite database files (Go to http://sqlitebrowser.org/ if you need one)
ls DailyDozenDB*.db | tail -1 | xargs open