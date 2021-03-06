package com.example.richo_han.notificationtest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.richo_han.notificationtest.SettingsActionProvider.PREFS_NAME;

public class MainActivity extends AppCompatActivity
        implements MenuItem.OnMenuItemClickListener, NewSettingsDialogFragment.NewSettingsDialogListener, EditSettingsDialogFragment.EditSettingsDialogListener {
    public static final String PREFS_LAST_SETTINGS = "NLastSettings";

    NotificationCompat.Builder mBuilder;
    NotificationCompat.Action mReplyAction;
    Timer mTimer;
    EditText delayText, periodText, countText;
    Switch replySwitch;

    int interval;
    boolean replyEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        replySwitch = (Switch) findViewById(R.id.reply_switch);
        replySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                replyEnabled = isChecked;
                setReplyEnabled(isChecked);
            }
        });

        loadLastSettings();

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (isInputsCompleted()) {
                    sendNotification();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter numbers!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                delayText.setText("");
                periodText.setText("");
                countText.setText("");
                replySwitch.setChecked(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_LAST_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("temp_delay", delayText.getText().toString());
        editor.putString("temp_period", periodText.getText().toString());
        editor.putString("temp_counts", countText.getText().toString());
        editor.putBoolean("temp_reply_mode", replySwitch.isChecked());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void loadLastSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_LAST_SETTINGS, 0);
        String tmpDelay = settings.getString("temp_delay", "");
        String tmpPeriod = settings.getString("temp_period", "");
        String tmpCounts = settings.getString("temp_counts", "");
        Boolean tmpChecked = settings.getBoolean("temp_reply_mode", false);
        loadSettings(tmpDelay, tmpPeriod, tmpCounts, tmpChecked);
    }

    private void loadSettings(String delay, String period, String counts, boolean checked) {
        delayText.setText(delay);
        periodText.setText(period);
        countText.setText(counts);
        replySwitch.setChecked(checked);
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

    /**
     * Count down the number of notification shown.
     * @return
     */
    private final int setInterval() {
        if (interval == 1)
            mTimer.cancel();
        return --interval;
    }

    private boolean isInputsCompleted() {
        if (delayText.getText().toString().length() < 1 ||
                periodText.getText().toString().length() < 1 ||
                countText.getText().toString().length() < 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if(item.getGroupId() == SettingsActionProvider.ADD_TO_SETTINGS) {

            if (isInputsCompleted()) {
                NewSettingsDialogFragment dialogFragment = NewSettingsDialogFragment.newInstance("Setting " + item.getItemId());
                dialogFragment.show(getSupportFragmentManager(), "NewSettingsDialogFragment");
            } else {
                Toast.makeText(getApplicationContext(), "Please enter numbers!", Toast.LENGTH_LONG).show();
            }
        } else if(item.getGroupId() == SettingsActionProvider.EDIT_SETTINGS) {

            EditSettingsDialogFragment dialogFragment = new EditSettingsDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "EditSettingsDialogFragment");
        } else {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String[] parameters = settings.getString(item.getTitle().toString(), "").split("/");
            loadSettings(parameters[0],
                    parameters[1],
                    parameters[2],
                    Boolean.parseBoolean(parameters[3]));
        }
        return true;
    }

    @Override
    public void onNewSettingsDialogPositiveClick(DialogFragment dialog, String name) {
        int delay, period, counts;
        delay = Integer.parseInt(delayText.getText().toString());
        period = Integer.parseInt(periodText.getText().toString());
        counts = Integer.parseInt(countText.getText().toString());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, delay + "/" + period + "/" + counts + "/" + replyEnabled);
        editor.commit();
        Toast.makeText(this, "Saved to settings!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditSettingsDialogPositiveClick(DialogFragment dialog, ArrayList<String> items) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        for(String key : items){
            editor.remove(key);
        }
        editor.commit();
        Toast.makeText(this, "Settings updated!", Toast.LENGTH_SHORT).show();
    }
}
