package com.vinay.androidagent

import android.app.Activity
import android.os.Bundle
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

        val commandInput = findViewById<EditText>(R.id.commandInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val resultText = findViewById<TextView>(R.id.resultText)

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

                    runOnUiThread {

                        resultText.text = response

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
}