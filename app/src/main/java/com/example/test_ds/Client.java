package com.example.test_ds;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;
import android.net.Uri;

import java.io.*;
import java.net.*;



public class Client extends Thread {

    private String gpxString;
    private String results;
    private Context context;



    Client(Context context, String gpx) {
        this.context = context;
        this.gpxString = gpx;
    }
    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Socket requestSocket = null;
        try {
            requestSocket = new Socket("192.168.1.8", 1234);

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream((requestSocket.getInputStream()));
            //send string to server
            out.writeUTF(gpxString);
            out.flush();
            //receive string from server
            results = in.readUTF();

            System.out.println("File received from server"+results);

            Intent intent = new Intent(context, StatisticsActivity.class);
            showNotification("Processing finished", "Tap here to analyze your statistics.", intent);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "results.txt");

            FileWriter writer = new FileWriter(file);
            writer.append(results);
            writer.flush();
            writer.close();


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (requestSocket != null) {
                    requestSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    private void showNotification(String title, String message, Intent intent) {
        // Create a notification channel
        String channelId = "my_channel";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);

        // Get the notification manager

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        // Create the pending intent with FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification_foreground)
                .setContentIntent(pendingIntent) // Set the pending intent to open StatisticsActivity
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }


    }

