package com.example.richo_han.notificationtest;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

/**
 * Created by richo on 2016/12/20.
 */

public class SettingsActionProvider extends ActionProvider implements OnMenuItemClickListener {
    private static final int ADD_TO_SETTINGS = 1;

    Context mContext;

    int mMenuIndex = 0;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public SettingsActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();

        subMenu
                .add(ADD_TO_SETTINGS, mMenuIndex, mMenuIndex, "Add to Settings")
                .setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getGroupId() == ADD_TO_SETTINGS) {
            Toast.makeText(mContext, "Saved to settings!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Loading settings...", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
