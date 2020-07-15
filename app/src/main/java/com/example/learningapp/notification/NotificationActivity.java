package com.example.learningapp.notification;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.learningapp.R;
import com.example.learningapp.intent.DisplayMessageActivity;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "Learningapp_Default_ID";
    public static final String CHANNEL_NAME = "new Message";
    public static final int NOTIFY_REQUEST_CODE = 0;
    public static int NOTIFY_ID = 123;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null){
                setChannel();
                Log.i("NotificationActivity", "channel set...");
            }
        }
        new Timer().schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                sendNotification(null);
            }
        }, 3000);
    }

    public void sendNotification(View view){
        Log.i("NotificationActivity", "notification init, version lt p");
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFY_REQUEST_CODE, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("标题")
                .setContentText("Hello world!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("hello world. addddddddddfaefafafafeafafafaffaafafafddadfasdgdfz"))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setTicker("ticker")
//                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        notificationManager.notify(NOTIFY_ID, notification);
//        sendNotificationLTP(view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
//            sendNotificationGTP(view);
//        }else{
//            sendNotificationLTP(view);
//        }
    }

    private void sendNotificationLTP(View view) {
        // Pending intent
        Log.i("NotificationActivity", "notification init, version lt p");
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFY_REQUEST_CODE, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(Icon.createWithResource(this, R.drawable.ic_launcher_foreground))
                .setContentTitle("标题")
                .setContentText("Hello world!")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
//                .setAutoCancel(true)
                .setTicker("new Message")
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        notificationManager.notify(NOTIFY_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void sendNotificationGTP(View view){
        // Pending intent
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFY_REQUEST_CODE, intent, 0);
        // MessageStyle
        Person p = new Person.Builder()
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background))
                .setName("myName")
                .build();
        Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(p);
        messagingStyle.setConversationTitle("一条新通知");
        Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(
                "hello!", System.currentTimeMillis(), p);
        messagingStyle.addMessage(message);

        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            notification = new Notification.Builder(this).build();
        else
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setStyle(messagingStyle)
                    .setContentTitle("标题")
                    .setContentText("Hello world!")
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("new Message")
                    .setContentIntent(pendingIntent)
                    .build();
        notificationManager.notify(NOTIFY_ID, notification);
        NOTIFY_ID += 1;
    }

    private void setChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager == null)
                return;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableLights(true);
//            channel.setLightColor(Color.BLUE);
//            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void alertSimpleDialog(View view){
        new AlertDialog.Builder(this)
                .setTitle("Simple Dialog")
                .setIcon(R.drawable.ic_launcher_foreground)
                .setSingleChoiceItems(new String[]{"a", "b", "c"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "select "+which, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "yes "+which, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "no "+which, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .create()
                .show();
    }

    public void alertDefinedDialog(View view){
        new AlertDialog.Builder(this)
                .setTitle("Simple Dialog")
                .setIcon(R.drawable.ic_launcher_foreground)
                .setView(getLayoutInflater().inflate(R.layout.activity_notification, null))
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "yes "+which, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "no "+which, Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .create()
                .show();
    }

}
