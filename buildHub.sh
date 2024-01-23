#!/usr/bin/env zsh

adb uninstall 'cn.mycommons.testplugin'
adb uninstall 'cn.mycommons.androidmodular'

hub -f buildHub.yaml --rebuild --log normal --save-in-project