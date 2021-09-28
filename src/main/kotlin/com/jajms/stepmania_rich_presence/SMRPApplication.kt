package com.jajms.stepmania_rich_presence

import com.dustinredmond.fxtrayicon.FXTrayIcon
import com.jajms.stepmania_rich_presence.discord.DiscordRichPresence
import com.jajms.stepmania_rich_presence.discord.DownloadNativeLibrary
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.system.exitProcess

class SMRPApplication : Application() {
    private lateinit var controller: ApplicationController
    private lateinit var fileHandler: FileHandler
    private lateinit var SMRPStage: Stage
    private val configPath: String = Paths.get("").toAbsolutePath().toString() + "\\config.txt"
    private var setupDone: Boolean = false
    private var stepmaniaPath: String = ""
    var isProcessRunning: Boolean = false
    var processTimestamp: Instant? = null
    var currentStatus: String = ""
    var discordRichPresence: DiscordRichPresence? = null
    var isRPCDisabled: Boolean = false

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(SMRPApplication::class.java.getResource("main-view.fxml"))
        controller = ApplicationController(this)
        fxmlLoader.setController(controller)

        SMRPStage = stage

        val icon = FXTrayIcon(stage, SMRPApplication::class.java.getResource("stepmania.png"))
        val closeItem = MenuItem("Close")
        closeItem.setOnAction {
            Platform.exit()
            closeApplication(0)
        }

        icon.addMenuItem(closeItem)

        val openItem = MenuItem("Show")
        openItem.setOnAction {
            stage.show()
        }

        icon.addMenuItem(openItem)
        icon.show()

        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "StepMania Rich Presence"
        stage.scene = scene
        stage.icons.add(Image(SMRPApplication::class.java.getResourceAsStream("stepmania.png")))
        stage.show()
        stage.setOnCloseRequest {
            stage.hide()
        }

        val discordLib = DownloadNativeLibrary.downloadDiscordLibrary()
        if (discordLib == null) {
            Alert(Alert.AlertType.ERROR, "Could not download the Discord GameSDK library.")
                .showAndWait()
            this.closeApplication(-2)
        }

        if (Path(configPath).exists()) {
            val config = FileHandler().readFileToList(configPath)
            //Needs to be completely remade from scratch
            for (line in config) {
                if (line.startsWith("setupDone=")) {
                    setupDone = (line.split("setupDone=")[0] == "true")
                    continue
                }
                if (line.startsWith("stepmaniaPath=")) {
                    stepmaniaPath = line.replace("stepmaniaPath=", "")
                    continue
                }
            }
        }
        else {
            File(configPath).createNewFile()
        }
        if (stepmaniaPath != "" && checkStepmaniaPath(Path(stepmaniaPath))) {
            if (!Patcher.checkPatch("$stepmaniaPath\\..", SMRPApplication::class.java.getResourceAsStream("richpresencemodule.lua"))) {
                val simplyLovePath = Patcher.getSimplyLovePath("$stepmaniaPath\\..")
                if (simplyLovePath != null &&!Patcher.patch(simplyLovePath,
                        String(SMRPApplication::class.java.getResourceAsStream("richpresencemodule.lua").readAllBytes(), StandardCharsets.UTF_8))) {
                    Alert(
                        Alert.AlertType.ERROR,
                        "Could not patch StepMania, please check if Simply Love is installed and has a \"Modules\" folder"
                    ).showAndWait()
                    closeApplication(-1)
                }
                else {
                    Alert(Alert.AlertType.INFORMATION, "StepMania has been patched.\nPlease restart StepMania.").showAndWait()
                }
            }
        }
        else {
            while (true) {
                val alert = Alert(Alert.AlertType.CONFIRMATION, "Please open StepMania before pressing the OK button.")
                alert.showAndWait()
                if (alert.result.text == "OK") {
                    stepmaniaPath = ProcessMonitor.getProcessPath("StepMania.exe")
                    if (stepmaniaPath.isNotEmpty()) {
                        File(configPath).writeText("setupDone=true\n" +
                                "stepmaniaPath=" + stepmaniaPath.replace("\\", "\\\\"))
                        break
                    }
                    else continue
                }
                else exitProcess(-1)
            }
        }

        discordRichPresence = DiscordRichPresence(discordLib)
        if (ProcessMonitor.getProcessPath("StepMania.exe") != "") {
            processTimestamp = Instant.now()
            isProcessRunning = true
            discordRichPresence?.setStatus(details="On the menus", timestamp=processTimestamp)
        }

        fileHandler = FileHandler()

        ProcessMonitor().monitorProcess("StepMania.exe") {
            isProcessRunning = it
            if (!isProcessRunning) {
                processTimestamp = null
                discordRichPresence?.clearStatus()
                fileHandler.stopMonitoringFile()
            }
            else {
                processTimestamp = Instant.now()
                if (!isRPCDisabled) {
                    Platform.runLater {
                        statusCallback("")
                    }
                }
                fileHandler.monitorFile(Path(stepmaniaPath).parent.toString() + "\\Themes\\Simply-Love-SM5-5.0.1\\richpresence.txt", 3000) { x ->
                    currentStatus = x
                    if (!isRPCDisabled)
                        statusCallback(x)
                }
            }
        }
    }

    fun statusCallback(status: String) {
        var details = status
        println(status)
        if (status == "")
            details = "On the menus"
        discordRichPresence?.setStatus(details=details, timestamp=processTimestamp)
        Platform.runLater {
            controller.setLabel(details)
        }
    }

    fun monitorRPCFile() {
        fileHandler.monitorFile(Path(stepmaniaPath).parent.toString() + "\\Themes\\Simply-Love-SM5-5.0.1\\richpresence.txt", 3000) { x ->
            if (!isRPCDisabled)
                statusCallback(x)
        }
    }

    private fun checkStepmaniaPath(path: Path): Boolean {
        if (!path.exists() || !File("$path\\StepMania.exe").exists())
            return false
        return true
    }

    fun closeApplication(exitCode: Int) {
        discordRichPresence?.close()
        Platform.exit()
        exitProcess(exitCode)
    }
}

fun main() {
    Application.launch(SMRPApplication::class.java)
}
