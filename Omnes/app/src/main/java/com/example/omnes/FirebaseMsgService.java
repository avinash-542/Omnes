package com.example.omnes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

public class FirebaseMsgService extends FirebaseMessagingService {
    public static int NotiID = 1;
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        
        generateNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());

    }

    private void generateNotification(String body, String title) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title1 = this.getString(R.string.app_name);
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.notiicon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Omnes_UNO")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(soundUri)
                .setContentIntent(pi);

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(NotiID > 1073741824) {
            NotiID = 0;
        }
        nm.notify(NotiID++, builder.build());
    }
}