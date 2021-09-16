package com.example.androidchat2.services.chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.androidchat2.R;
import com.example.androidchat2.utils.Logs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    protected NotificationChannel newMessageChannel = null;

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        Logs.debug(this, "new token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Logs.debug(this, "from: " + remoteMessage.getFrom());

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String groupId = remoteMessage.getData().get("groupId");

        if (notification != null && groupId != null) {
            sendNotification(notification, groupId);
        }
    }

    private void sendNotification(RemoteMessage.Notification notificationMessage, String groupId) {
        PendingIntent pendingIntent = getGroupPendingIntent(groupId);

        NotificationChannel newMessageChannel = getNewMessageNotificationChannel();

        Notification notification = getNewMessageNotification(newMessageChannel, notificationMessage, pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(newMessageChannel);
        notificationManager.notify(0, notification);
    }

    public PendingIntent getGroupPendingIntent(String groupId) {
        Bundle intentBundle = new Bundle();
        intentBundle.putString("group_id", groupId);

        return new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.chatMessagesFragment)
                .setArguments(intentBundle)
                .createPendingIntent();
    }

    public NotificationChannel getNewMessageNotificationChannel() {
        if (newMessageChannel == null) {
            String channelId = getString(R.string.new_message_notification_channel_id);

            newMessageChannel = new NotificationChannel(channelId,
                    "Chat notification channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            newMessageChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        }

        return newMessageChannel;
    }

    public Notification getNewMessageNotification(NotificationChannel notificationChannel, RemoteMessage.Notification notificationMessage, PendingIntent pendingIntent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat
                .Builder(this, notificationChannel.getId())
                .setSmallIcon(R.drawable.ic_send_primary)
                .setContentTitle(notificationMessage.getTitle())
                .setContentText(notificationMessage.getBody())
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.blue_600, null))
                .build();
    }


}
