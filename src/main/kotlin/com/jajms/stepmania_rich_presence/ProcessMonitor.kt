package com.jajms.stepmania_rich_presence

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessMonitor {
    private var processStatus: Boolean = false

    fun monitorProcess(filter: String, waitTime: Long = 2000, cb: ((Boolean)->Unit)) {
        GlobalScope.launch {
            //cb.invoke(processStatus)
            while (true) {
                if ((getProcessPath(filter) != "") == !processStatus) {
                    processStatus = !processStatus
                    cb.invoke(processStatus)
                }

                delay(waitTime)
            }
        }
    }
    companion object {
        fun getProcessPath(filter: String): String {
            val process = ProcessHandle.allProcesses().filter {
                if (it.info().command().isPresent)
                    it.info().command().get().endsWith(filter)
                else
                    false
            }.findFirst()
            if (process.isPresent) {
                return process.get().info().command().get().removeSuffix("\\$filter")
            }
            return ""
        }
    }
}