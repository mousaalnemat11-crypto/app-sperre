package com.example.appsperre

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BlockerService : AccessibilityService() {

    private val blocked = setOf(
        "com.android.vending",
        "com.google.android.apps.photos",
        "com.sec.android.gallery3d",
        "com.android.gallery3d",
        "com.google.android.gallery3d",
        "com.miui.gallery",
        "com.oneplus.gallery",
        "com.coloros.gallery3d",
        "com.brave.browser",
        "com.brave.browser_beta",
        "com.brave.browser_nightly"
    )

    private val whatsapp = setOf("com.whatsapp", "com.whatsapp.w4b")

    private val protectedScreens = setOf(
        "com.android.settings",
        "com.google.android.packageinstaller",
        "com.android.packageinstaller",
        "com.samsung.android.packageinstaller",
        "com.google.android.permissioncontroller",
        "com.android.permissioncontroller"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        when {
            pkg in blocked -> performGlobalAction(GLOBAL_ACTION_HOME)
            pkg in whatsapp -> checkWhatsApp(pkg)
            pkg in protectedScreens -> checkProtected()
        }
    }

    private fun checkProtected() {
        val root = rootInActiveWindow ?: return
        if (containsAppName(root, 0)) performGlobalAction(GLOBAL_ACTION_HOME)
    }

    private fun containsAppName(node: AccessibilityNodeInfo?, depth: Int): Boolean {
        if (node == null || depth > 25) return false
        val t = node.text?.toString() ?: ""
        val d = node.contentDescription?.toString() ?: ""
        if (t.contains("App-Sperre", true) || d.contains("App-Sperre", true)) return true
        for (i in 0 until node.childCount) {
            if (containsAppName(node.getChild(i), depth + 1)) return true
        }
        return false
    }

    private fun checkWhatsApp(pkg: String) {
        val root = rootInActiveWindow ?: return
        val nodes = root.findAccessibilityNodeInfosByViewId("$pkg:id/conversation_contact_name")
        val raw = nodes.firstOrNull()?.text?.toString() ?: return
        val name = Config.norm(raw)
        if (Config.allowed(this).none { it.equals(name, ignoreCase = true) }) {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    override fun onInterrupt() {}
}
