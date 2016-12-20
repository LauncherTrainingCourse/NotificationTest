package com.example.richo_han.notificationtest;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.View;

/**
 * Created by richo on 2016/12/20.
 */

public class SettingsActionProvider extends ActionProvider {
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public SettingsActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        Log.d("Tag", "ActionProvider loading.");
        return null;
    }
}
