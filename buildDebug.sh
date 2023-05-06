#!/usr/bin/env zsh

set -v on

./gradlew clean
#git clean -xdf
./gradlew --stop

set -e

./gradlew :TestPlugin:TestPluginApp:assembleDebug --profile --no-daemon --no-build-cache -s
#./gradlew :TestPluginApp:assembleDebug --profile --no-daemon --no-build-cache -s -w -Dorg.gradle.debug=true

#exit
adb uninstall cn.mycommons.testplugin || echo ""
adb install -r TestPlugin/TestPluginApp/build/outputs/apk/debug/TestPluginApp-debug.apk
sleep 1
adb shell am start -n cn.mycommons.testplugin/.MainActivity
