#!/usr/bin/env zsh

set -v on

./gradlew clean
#git clean -xdf
./gradlew --stop

set -e

./gradlew :TestModule:app:assembleDebug --profile --no-daemon --no-build-cache -s
#./gradlew :TestPluginApp:assembleDebug --profile --no-daemon --no-build-cache -s -w -Dorg.gradle.debug=true

#exit
adb uninstall cn.mycommons.testplugin || echo ""
adb install -r TestModule/app/build/outputs/apk/debug/app-debug.apk
