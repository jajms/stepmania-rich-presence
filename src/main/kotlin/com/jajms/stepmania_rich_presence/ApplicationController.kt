package com.jajms.stepmania_rich_presence

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label

class ApplicationController(a: SMRPApplication) {
    private val application = a
    @FXML
    private lateinit var Label1: Label
    @FXML
    private lateinit var Button1: Button

    @FXML
    fun onButtonClick() {
        if (application.isRPCDisabled) {
            Button1.text = "Disable Rich Presence"
            application.isRPCDisabled = false
            if (application.isProcessRunning) {
                application.statusCallback(application.currentStatus)
                application.monitorRPCFile()
            }
        }
        else {
            Button1.text = "Enable Rich Presence"
            application.isRPCDisabled = true
            application.discordRichPresence?.clearStatus()
        }

    }

    fun setLabel(label: String) {
        Label1.text = label
    }
}