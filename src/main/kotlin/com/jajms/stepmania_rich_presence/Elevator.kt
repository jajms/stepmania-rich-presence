package com.jajms.stepmania_rich_presence

import com.sun.jna.platform.win32.Shell32
import com.sun.jna.platform.win32.WinUser.SW_SHOWNORMAL
import java.nio.file.Paths
import kotlin.system.exitProcess

fun elevate(): Boolean {
    val processName = ProcessHandle.current().info().command().get().lowercase()
    if (processName.endsWith("java.exe") || processName.endsWith("java.exe\\")) {
        println("Can only be called from an .exe file!")
        return false
    }

    Shell32.INSTANCE.ShellExecute(null, "runas", Paths.get("").toAbsolutePath().toString() + "\\smrp.exe", null, null, SW_SHOWNORMAL)

    exitProcess(0)
}