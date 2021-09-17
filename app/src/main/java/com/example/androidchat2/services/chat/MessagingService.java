package com.example.androidchat2.services.chat;

import android.app.Notification;
import android.app.PendingIntent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.androidchat2.R;
import com.example.androidchat2.utils.Logs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    protected NotificationChannelCompat newMessageChannel = null;

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        Logs.debug(this, "new token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Logs.debug(this, "from: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("groupId")) {
            sendNotification(data);
        }
    }

    private void sendNotification(Map<String, String> data) {
        String groupId = data.get("groupId");
        PendingIntent pendingIntent = getGroupPendingIntent(groupId);

        NotificationChannelCompat newMessageChannel = getNewMessageNotificationChannel();

        Notification notification = getNewMessageNotification(newMessageChannel, data, pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
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

    public NotificationChannelCompat getNewMessageNotificationChannel() {
        if (newMessageChannel == null) {
            String channelId = getString(R.string.new_message_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build();

            newMessageChannel = new NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("Chat message channel")
                    .setVibrationEnabled(true)
                    .setVibrationPattern(new long[]{0, 500, 1000, 500})
                    .setShowBadge(true)
                    .setSound(defaultSoundUri, audioAttributes)
                    .build();
        }

        return newMessageChannel;
    }

    public Notification getNewMessageNotification(NotificationChannelCompat notificationChannel,
                                                  Map<String, String> data,
                                                  PendingIntent pendingIntent) {
        String title = data.get("title");
        String body = data.get("body");

        return new NotificationCompat
                .Builder(this, notificationChannel.getId())
                .setSmallIcon(R.drawable.ic_send_primary)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.blue_600, null))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(1)
                .build();
    }


}
