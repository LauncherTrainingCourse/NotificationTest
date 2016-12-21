package com.example.richo_han.notificationtest;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewSettingsDialogFragment extends DialogFragment {


    public interface NewSettingsDialogListener {
        void onNewSettingsDialogPositiveClick(DialogFragment dialog, String name);
    }

    NewSettingsDialogListener mListener;
    View mView;
    EditText mEditText;
    Button mButton;

    public static NewSettingsDialogFragment newInstance(String name) {
        NewSettingsDialogFragment dialogFragment = new NewSettingsDialogFragment();

        Bundle args = new Bundle();
        args.putString("Name", name);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (NewSettingsDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewSettingsDialogListener");
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_new_settings_dialog, null);
        mEditText = (EditText) mView.findViewById(R.id.settings_name);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEditText.setText(getArguments().getString("Name"));
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0) {
                    mButton.setEnabled(true);
                } else {
                    mButton.setEnabled(false);
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.new_settings_dialog_title)
                .setView(mView)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = mEditText.getText().toString();
                        if(!name.isEmpty()) {
                            mListener.onNewSettingsDialogPositiveClick(NewSettingsDialogFragment.this, name);
                        } else {
                            mEditText.setError("The name should not be blank!");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        final AlertDialog mAlertDialog = builder.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                mButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            }
        });

        return mAlertDialog;
    }

}
