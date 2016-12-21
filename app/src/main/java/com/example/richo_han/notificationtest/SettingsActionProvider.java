package com.example.richo_han.notificationtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by richo on 2016/12/20.
 */

public class SettingsActionProvider extends ActionProvider {
    public static final int LOAD_FROM_SETTINGS = 0;
    public static final int ADD_TO_SETTINGS = 1;
    public static final int EDIT_SETTINGS = 2;
    public static final String PREFS_NAME = "NTestSettings";

    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    Context mContext;
    OnMenuItemClickListener mListener;

    int mMenuIndex = 1;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public SettingsActionProvider(Context context) {
        super(context);
        mContext = context;

        try {
            mListener = (OnMenuItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RatingDialogListener");
        }
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
        mMenuIndex = 1;
        subMenu.clear();

        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        Collection<String> unSortedKeys = settings.getAll().keySet();
        List<String> sortedKeys = SettingsActionProvider.asSortedList(unSortedKeys);

        for(String key : sortedKeys){
            subMenu
                    .add(LOAD_FROM_SETTINGS, mMenuIndex, mMenuIndex, key)
                    .setOnMenuItemClickListener(mListener);
            mMenuIndex++;
        }

        subMenu
                .add(ADD_TO_SETTINGS, mMenuIndex, mMenuIndex, "Add to Settings")
                .setOnMenuItemClickListener(mListener);

        subMenu
                .add(EDIT_SETTINGS, mMenuIndex+1, mMenuIndex+1, "Remove Settings")
                .setOnMenuItemClickListener(mListener);
    }
}
