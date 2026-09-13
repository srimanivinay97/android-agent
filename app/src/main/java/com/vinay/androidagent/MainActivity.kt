package com.vinay.androidagent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    private val serverUrl =
        "http://192.168.55.103:8000/agent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppUpdater(this).checkForUpdate()
        val commandInput =
            findViewById<EditText>(R.id.commandInput)

        val sendButton =
            findViewById<Button>(R.id.sendButton)

        val resultText =
            findViewById<TextView>(R.id.resultText)

        sendButton.setOnClickListener {

            resultText.text = "Sending..."

            Thread {

                try {

                    val url = URL(serverUrl)

                    val connection =
                        url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"

                    connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )

                    connection.doOutput = true

                    val requestJson = JSONObject()

                    requestJson.put(
                        "message",
                        commandInput.text.toString()
                    )

                    connection.outputStream.use {
                        it.write(
                            requestJson
                                .toString()
                                .toByteArray()
                        )
                    }

                    val response =
                        connection.inputStream
                            .bufferedReader()
                            .readText()

                    val actionJson =
                        JSONObject(response)

                    runOnUiThread {

                        resultText.text =
                            actionJson.toString(2)

                        executeAction(actionJson)
                    }

                } catch (e: Exception) {

                    runOnUiThread {
                        resultText.text =
                            "ERROR: ${e.message}"
                    }
                }

            }.start()
        }
    }

    private fun executeAction(actionJson: JSONObject) {

        val service =
            AgentAccessibilityService.instance

        if (service == null) {

            val intent =
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)

            startActivity(intent)

            return
        }

        when (actionJson.optString("action")) {

            "open_app" -> {

                val app =
                    actionJson.optString("app")

                val success =
                    service.openApp(app)

                if (!success) {
                    findViewById<TextView>(R.id.resultText).text =
                        "Could not open app: $app"
                }
            }

            "back" -> {
                service.goBack()
            }

            "home" -> {
                service.goHome()
            }

            "done" -> {
                // Nothing to execute
            }

            else -> {

                findViewById<TextView>(R.id.resultText).text =
                    "Unsupported action: ${actionJson.optString("action")}"
            }
        }
    }
}