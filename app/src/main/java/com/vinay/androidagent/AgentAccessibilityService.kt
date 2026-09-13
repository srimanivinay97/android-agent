package com.vinay.androidagent

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AgentAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AgentAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We will use this later to read the UI.
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun goBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun goHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun openApp(appName: String): Boolean {

        val packageManager = packageManager

        val installedApps =
            packageManager.getInstalledApplications(0)

        val targetApp = installedApps.firstOrNull { app ->
            val label =
                packageManager
                    .getApplicationLabel(app)
                    .toString()

            label.equals(appName, ignoreCase = true)
        } ?: return false

        val launchIntent =
            packageManager.getLaunchIntentForPackage(
                targetApp.packageName
            ) ?: return false

        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        startActivity(launchIntent)

        return true
    }
}