#!/usr/bin/env bash

# https://gitlab.com/fdroid/fdroiddata/blob/master/README.md
# https://gitlab.com/fdroid/fdroiddata/blob/master/CONTRIBUTING.md

set -ue

appid="org.nutritionfacts.dailydozen"

if [[ ! -d fdroidserver ]]; then
    echo "Cloning fdroidserver"
    git clone https://gitlab.com/fdroid/fdroidserver.git
    export PATH="$PATH:$PWD/fdroidserver"
fi

if [[ ! -d fdroiddata ]]; then
    echo "Cloning fdroiddata"
    git clone https://gitlab.com/fdroid/fdroiddata.git
fi

pushd fdroiddata

echo "Create an empty config file"
touch config.py

echo "Make sure fdroid works and reads the metadata files properly"
fdroid readmeta

echo "Cleaning up metadata file"
fdroid rewritemeta "$appid"

echo "Filling automated fields in metadata file (e.g. Auto Name and Current Version)"
fdroid checkupdates --auto "$appid"

echo "Making sure that fdroid lint doesn't report any warnings. If it does, fix them."
fdroid lint "$appid"

echo "Testing build recipe"
fdroid build -v -l "$appid"

popd
