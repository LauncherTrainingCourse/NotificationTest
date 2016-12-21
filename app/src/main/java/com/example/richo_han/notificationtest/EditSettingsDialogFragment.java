package com.example.richo_han.notificationtest;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collection;

import static com.example.richo_han.notificationtest.SettingsActionProvider.PREFS_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditSettingsDialogFragment extends DialogFragment {
    public interface EditSettingsDialogListener {
        void onEditSettingsDialogPositiveClick(DialogFragment dialog, ArrayList<String> items);
    }

    EditSettingsDialogListener mListener;
    ArrayList<String> mSelectedItems;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (EditSettingsDialogFragment.EditSettingsDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EditSettingsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Collection<String> unSortedKeys = settings.getAll().keySet();
        final CharSequence[] sortedKeys = SettingsActionProvider.asSortedList(unSortedKeys).toArray(new CharSequence[unSortedKeys.size()]);

        builder
                .setTitle(R.string.edit_settings_dialog_title)
                .setMultiChoiceItems(sortedKeys, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(sortedKeys[which].toString());
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onEditSettingsDialogPositiveClick(EditSettingsDialogFragment.this, mSelectedItems);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

}
