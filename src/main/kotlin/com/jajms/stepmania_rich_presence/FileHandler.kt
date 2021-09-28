package com.jajms.stepmania_rich_presence

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class FileHandler() {
    private var file: File? = null
    private var fileContent = ""
    private var coroutine: Job? = null

    fun monitorFile(path: String, waitTime: Long = 1000, cb: (String)->Unit): FileHandler {
        coroutine = GlobalScope.launch {
            while (true) {
                val str = readFile(path)
                if (str != fileContent) {
                    fileContent = str
                    cb.invoke(fileContent)
                }

                delay(waitTime)
            }
        }
        return this
    }

    fun stopMonitoringFile() {
        coroutine?.cancel()
    }

    fun readFile(path: String): String {
        if (file?.path != path) {
            file = File(path)
        }
        return file!!.readText()
    }

    fun readFileToList(path: String): List<String> {
        if (file?.path != path) {
            file = File(path)
        }
        return file!!.readLines()
    }
}