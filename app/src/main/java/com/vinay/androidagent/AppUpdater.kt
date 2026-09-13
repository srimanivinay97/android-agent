package com.vinay.androidagent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class AppUpdater(
    private val activity: Activity
) {

    fun checkForUpdate() {

        Thread {

            try {

                val repo =
                    activity.getString(
                        R.string.github_repository
                    )

                val apiUrl =
                    "https://api.github.com/repos/$repo/releases/latest"

                val connection =
                    URL(apiUrl).openConnection()
                            as HttpURLConnection

                connection.setRequestProperty(
                    "Accept",
                    "application/vnd.github+json"
                )

                connection.setRequestProperty(
                    "User-Agent",
                    "Android-Agent"
                )

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .readText()

                val release =
                    JSONObject(response)

                val tag =
                    release.getString("tag_name")

                val remoteBuild =
                    tag
                        .removePrefix("build-")
                        .toIntOrNull()
                        ?: return@Thread

                val currentBuild =
                    getCurrentVersionCode()

                if (remoteBuild > currentBuild) {

                    val assets =
                        release.getJSONArray("assets")

                    for (i in 0 until assets.length()) {

                        val asset =
                            assets.getJSONObject(i)

                        if (
                            asset.getString("name") ==
                            "Android-Agent.apk"
                        ) {

                            val downloadUrl =
                                asset.getString(
                                    "browser_download_url"
                                )

                            downloadAndInstall(
                                downloadUrl
                            )

                            break
                        }
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }

        }.start()
    }

    private fun getCurrentVersionCode(): Int {

        val info =
            activity.packageManager
                .getPackageInfo(
                    activity.packageName,
                    0
                )

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.P
        ) {

            info.longVersionCode.toInt()

        } else {

            @Suppress("DEPRECATION")
            info.versionCode
        }
    }

    private fun downloadAndInstall(
        downloadUrl: String
    ) {

        val updateDir =
            File(
                activity.filesDir,
                "updates"
            )

        updateDir.mkdirs()

        val apkFile =
            File(
                updateDir,
                "Android-Agent.apk"
            )

        val connection =
            URL(downloadUrl)
                .openConnection()
                    as HttpURLConnection

        connection.instanceFollowRedirects = true

        connection.inputStream.use { input ->

            apkFile.outputStream().use { output ->

                input.copyTo(output)
            }
        }

        activity.runOnUiThread {

            installApk(apkFile)
        }
    }

    private fun installApk(
        apkFile: File
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O &&
            !activity.packageManager
                .canRequestPackageInstalls()
        ) {

            val intent =
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse(
                        "package:${activity.packageName}"
                    )
                )

            activity.startActivity(intent)

            return
        }

        val apkUri =
            FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                apkFile
            )

        val intent =
            Intent(
                Intent.ACTION_VIEW
            ).apply {

                setDataAndType(
                    apkUri,
                    "application/vnd.android.package-archive"
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        activity.startActivity(intent)
    }
}