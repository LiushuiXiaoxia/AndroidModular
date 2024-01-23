#!/usr/bin/env zsh

set -v on

./gradlew clean
#git clean -xdf
./gradlew --stop

set -e

./gradlew :test-plugin:testpluginapp:assembledebug --profile --no-daemon --no-build-cache -s
#./gradlew :TestPluginApp:assembleDebug --profile --no-daemon --no-build-cache -s -w -Dorg.gradle.debug=true

#exit
adb uninstall cn.mycommons.testplugin || echo ""
adb install -r test-plugin/testpluginapp/build/outputs/apk/debug/testpluginapp-debug.apk
sleep 1
adb shell am start -n cn.mycommons.testplugin/.MainActivity
