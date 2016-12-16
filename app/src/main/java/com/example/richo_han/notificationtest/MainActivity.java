package com.example.richo_han.notificationtest;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    NotificationCompat.Builder mBuilder;
    Timer mTimer;
    int interval;
    EditText delayText, durationText, timesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delayText = (EditText) findViewById(R.id.delay);
        durationText = (EditText) findViewById(R.id.duration);
        timesText = (EditText) findViewById(R.id.times);

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_ring)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Button button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
    }

    public void sendNotification() {
        int delay = 1000, duration = 1000, times = 5;
        delay = Integer.parseInt(delayText.getText().toString());
        duration = Integer.parseInt(durationText.getText().toString());
        times = Integer.parseInt(timesText.getText().toString());
        sendNotification(delay, duration, times);
    }

    public void sendNotification(int delay, int duration, int times) {
        interval = times;
        mTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);

                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(interval, mBuilder.build());

                setInterval();
            }
        };

        mTimer.schedule(task, delay, duration);
    }

    private final int setInterval() {
        if (interval == 1)
            mTimer.cancel();
        return --interval;
    }
}
