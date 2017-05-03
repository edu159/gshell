package com.example.eduardo.gshell;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ryo on 17/04/17.
 */

public class fragmentDialogHostEntry extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment
     * must implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs
     * to query it.
     */


    public interface HostEntryDialogListener {
        public void onHostEntryDialogPositiveClick(Host newEntry);
    }

    // Use this instance of the interface to deliver action events
    HostEntryDialogListener mListener;

    // override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (HostEntryDialogListener) getActivity();
        } catch (ClassCastException e) {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
            +" must implement HostEntryDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        // Instantiating an AlertDialog.Builder object with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppCompatAlertDialogStyle);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_dialog_host_entry,null);
        builder.setView(dialogView);
        // chaining together various setter methods to set the dialog characteristics
        builder.setTitle("Enter host details")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // send the positive button event back to the host activity
                        EditText aliasEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_alias);
                        EditText addressEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_address);
                        EditText usernameEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_username);
                        EditText passwordEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_password);

                        Host newEntry = new Host();
                        newEntry.setAlias(aliasEditText.getText().toString());
                        newEntry.setAddress(addressEditText.getText().toString());
                        newEntry.setUsername(usernameEditText.getText().toString());
                        newEntry.setPassword(passwordEditText.getText().toString());

                        mListener.onHostEntryDialogPositiveClick(newEntry);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setNeutralButton("Test", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog



                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
