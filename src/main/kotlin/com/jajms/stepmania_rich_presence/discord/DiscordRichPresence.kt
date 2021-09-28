package com.jajms.stepmania_rich_presence.discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant

class DiscordRichPresence(lib: File) {
    private var core: Core?
    private val coroutine: Job
    //private lateinit var params: CreateParams

    init {
        Core.init(lib)

        val params = CreateParams()
        params.clientID = 860484783426764810
        params.flags = CreateParams.Flags.toLong(CreateParams.Flags.NO_REQUIRE_DISCORD)

        core = Core(params)

        coroutine = GlobalScope.launch {
            while (true) {
                try {
                    core?.runCallbacks()
                }
                catch (e: Exception){
                    println(e.stackTrace)
                }
                delay(200)
            }
        }
    }

    fun setStatus(details: String = "", timestamp: Instant?) {
        val activity = Activity()
        activity.details = details
        if (timestamp != null) {
            activity.timestamps().start = timestamp
        }
        activity.assets().largeImage = "stepmania_final"

        checkCore().activityManager()?.updateActivity(activity)
    }

    fun clearStatus() {
        core?.close()
        core = null
    }

    private fun checkCore(): Core {
        if (core == null) {
            val params = CreateParams()
            params.clientID = 860484783426764810
            params.flags = CreateParams.Flags.toLong(CreateParams.Flags.NO_REQUIRE_DISCORD)
            core = Core(params)
        }

        return core!!
    }

    fun close() {
        coroutine.cancel()
        core?.close()
    }
}