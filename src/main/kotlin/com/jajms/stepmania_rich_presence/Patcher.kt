package com.jajms.stepmania_rich_presence

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class Patcher {
    companion object {
        fun checkPatch(stepmaniaPath: String, luaModule: InputStream): Boolean {
            val simplyLovePath: String? = getSimplyLovePath(stepmaniaPath)
            val luaModuleStr = String(luaModule.readAllBytes(), StandardCharsets.UTF_8)

            if (simplyLovePath == null) {
                println("Simply Love is not Installed!")
                return false
            }

            return checkSimplyLovePatch(simplyLovePath, luaModuleStr)
        }

        fun getSimplyLovePath(stepmaniaPath: String): String? {
            val fileList = File(stepmaniaPath + "\\Themes").listFiles()

            for (file in fileList) {
                if (file.absolutePath.contains("Simply-Love-SM5-5")) {
                    return file.absolutePath
                }
            }

            return null
        }

        fun checkAndPatch(stepmaniaPath: String, luaModule: InputStream): Boolean {
            val simplyLovePath: String? = getSimplyLovePath(stepmaniaPath)
            val luaModuleStr = String(luaModule.readAllBytes(), StandardCharsets.UTF_8)

            if (simplyLovePath == null) {
                println("Simply Love is not Installed!")
                return false
            }

            if (!checkSimplyLovePatch(simplyLovePath, luaModuleStr)) {
                if (patch(simplyLovePath, luaModuleStr))
                    return true
                else {
                    println("Could not patch!")
                    return false
                }
            }

            return true
        }

        private fun checkSimplyLovePatch(simplyLovePath: String, luaModule: String): Boolean {
            if (!File("$simplyLovePath\\richpresence.txt").exists())
                return false

            val moduleFile = File("$simplyLovePath\\Modules\\rich-presence-module.lua")
            if (moduleFile.exists())
                return moduleFile.readText() == luaModule

            return false
        }

        fun patch(simplyLovePath: String, luaModule: String): Boolean {
            if (!File("$simplyLovePath\\richpresence.txt").exists())
                try {
                    File("$simplyLovePath\\richpresence.txt").createNewFile()
                }
                catch (e: IOException) {
                    if (!elevate())
                        return false
                }

            val moduleFile = File("$simplyLovePath\\Modules\\rich-presence-module.lua")
            if (moduleFile.exists() && Files.isWritable(moduleFile.toPath()))
                moduleFile.writeText(luaModule)
            else if (moduleFile.exists() && !Files.isWritable(moduleFile.toPath()))
                return elevate()
            else
                try {
                    moduleFile.createNewFile()
                    moduleFile.writeText(luaModule)
                }
                catch (e: Exception) {
                    if (!elevate())
                        return false
                }


            return true
        }
    }
}