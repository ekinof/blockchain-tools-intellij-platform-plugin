package com.github.ekinof.blockchaintools.actions.eth

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

internal fun notifyBalloon(project: Project?, message: String, type: NotificationType) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup("BlockchainTools")
        .createNotification(message, type)
        .notify(project)
}
