package com.example.richo_han.notificationtest;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    NotificationCompat.Builder mBuilder;
    NotificationCompat.Action mReplyAction;
    Timer mTimer;
    EditText delayText, periodText, countText;

    int interval;
    boolean mReplyMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delayText = (EditText) findViewById(R.id.delay);
        periodText = (EditText) findViewById(R.id.period);
        countText = (EditText) findViewById(R.id.counts);

        // Notification Customization
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_ring)
                        .setContentTitle("Notification Testing")
                        .setContentText("Customized Notification")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_ring));

        Switch replySwitch = (Switch) findViewById(R.id.reply_switch);
        replySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setReplyEnabled(isChecked);
            }
        });

        Button button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (delayText.getText().toString().length() < 1 ||
                        periodText.getText().toString().length() < 1 ||
                        countText.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please enter numbers!", Toast.LENGTH_LONG).show();
                } else {
                    sendNotification();
                }
            }
        });
    }

    /**
     * If enabled, add reply action to the Notification Builder.
     * @param enabled
     */
    private void setReplyEnabled(boolean enabled) {
        if(enabled) {
            String replyLabel = "Reply";
            RemoteInput remoteInput =
                    new RemoteInput.Builder(NotificationActivity.KEY_TEXT_REPLY)
                            .setLabel(replyLabel)
                            .build();

            Intent resultIntent = new Intent(this, NotificationActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(NotificationActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent pendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mReplyAction =
                    new NotificationCompat.Action.Builder(
                            R.drawable.ic_ring,
                            replyLabel,
                            pendingIntent)
                            .addRemoteInput(remoteInput)
                            .setAllowGeneratedReplies(true)
                            .build();

            mBuilder
                    .addAction(mReplyAction)
                    .setContentIntent(pendingIntent);
        } else {
            mBuilder.mActions.clear();
        }
    }

    private void sendNotification() {
        int delay, period, counts;
        delay = Integer.parseInt(delayText.getText().toString());
        period = Integer.parseInt(periodText.getText().toString());
        counts = Integer.parseInt(countText.getText().toString());
        sendNotification(delay, period, counts);
    }

    /**
     * Send notification based on delay, period, and counts.
     * delay:   Delay in milliseconds before task is to be executed.
     * period:  Time in milliseconds between successive task executions.
     * counts:  Number of tasks you want the app to show for you.
     * @param delay
     * @param period
     * @param counts
     */
    private void sendNotification(int delay, int period, int counts) {
        interval = counts;
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
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(interval, mBuilder.build());

                setInterval();
            }
        };

        mTimer.schedule(task, delay, period);
    }

    private final int setInterval() {
        if (interval == 1)
            mTimer.cancel();
        return --interval;
    }
}
