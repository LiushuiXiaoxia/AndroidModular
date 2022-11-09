package cn.mycommons.module_plugin.util

import java.io.File


fun File.safeRename(dest: File) {
    kotlin.runCatching {
        dest.safeDelete()
        renameTo(dest)
    }
}

fun File?.safeDelete() {
    if (this != null && exists()) {
        if (isDirectory) {
            deleteRecursively()
        } else {
            delete()
        }
    }
}

fun File?.isEmptyDir(): Boolean {
    if (this != null && this.isDirectory) {
        return list().isNullOrEmpty()
    }
    return false
}

fun File?.isNotEmptyDir(): Boolean {
    return !this.isEmptyDir()
}
