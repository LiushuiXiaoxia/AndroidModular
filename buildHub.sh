#!/usr/bin/env zsh

adb uninstall cn.mycommons.testplugin
hub -f buildHub.yaml --rebuild --log normal --save-in-project