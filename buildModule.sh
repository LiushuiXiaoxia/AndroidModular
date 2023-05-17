#!/usr/bin/env zsh

set -v on

./gradlew clean
#git clean -xdf
./gradlew --stop

set -e

./gradlew :test-module:app:assembleDebug --profile --no-daemon --no-build-cache -s
#./gradlew :TestPluginApp:assembleDebug --profile --no-daemon --no-build-cache -s -w -Dorg.gradle.debug=true

#exit
adb uninstall cn.mycommons.androidmodular || echo ""
adb install -r test-module/app/build/outputs/apk/debug/app-debug.apk
sleep 1
adb shell am start -n cn.mycommons.androidmodular/.ui.MainActivity