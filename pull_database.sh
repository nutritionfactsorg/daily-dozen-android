# Ensure we are running as root. Otherwise we will not be able to access the database file.
adb root

# TODO change this to accept the IP address from the command line. If none is present, assume it's a USB connection
#adb connect 192.168.1.195

# Download the database file to the current working directory.
adb pull /data/data/org.slavick.dailydozen/databases/dailydozen.db

# Append the current date and time to the file. This way we won't overwrite previously pulled database files.
mv dailydozen.db dailydozen.$(date +"%Y%m%d%H%M%S").db
