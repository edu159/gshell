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
 * Created by ryo on 19/04/17.
 */

public class fragmentDialogHostEdit extends DialogFragment {

    private int hostid;
    private DatabaseHandler db;

    static fragmentDialogHostEdit newInstance(int hostid) {
        fragmentDialogHostEdit frag = new fragmentDialogHostEdit();

        Bundle bundle = new Bundle();
        bundle.putInt("hostID", hostid);
        frag.setArguments(bundle);
        return frag;
    }
    public interface HostEditDialogListener {
        void onHostEditDialogPositiveClick(Host newEntry);
    }

    // Use this instance of the interface to deliver action events
    HostEditDialogListener mListener;

    // override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (HostEditDialogListener) getActivity();
        } catch (ClassCastException e) {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    +" must implement HostEntryDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hostid = getArguments().getInt("hostID");
        db = new DatabaseHandler(getActivity());
        final Host host = db.getHost(hostid);

        if (host == null){

        }


        // Use the Builder class for convenient dialog construction

        // Instantiating an AlertDialog.Builder object with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppCompatAlertDialogStyle);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Populating the text fields in the DialogFragment with the existing host info
        final View dialogView = inflater.inflate(R.layout.fragment_dialog_host_entry,null);
        final EditText aliasEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_alias);
        final EditText addressEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_address);
        final EditText usernameEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_username);
        final EditText passwordEditText = (EditText) dialogView.findViewById(R.id.edit_dialog_text_password);

        aliasEditText.setText(host._alias);
        usernameEditText.setText(host._username);
        addressEditText.setText(host._address);
        passwordEditText.setText(host._password);
        builder.setView(dialogView);
        // chaining together various setter methods to set the dialog characteristics
        builder.setTitle("Edit host details")

                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // send the positive button event back to the host activity

                        host.setAlias(aliasEditText.getText().toString());
                        host.setAddress(addressEditText.getText().toString());
                        host.setUsername(usernameEditText.getText().toString());
                        host.setPassword(passwordEditText.getText().toString());

                        mListener.onHostEditDialogPositiveClick(host);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }




    /*

    public interface HostEditDialogListener {
        void onHostEditDialogPositiveClick(Host editedEntry);
    }
    HostEditDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (HostEditDialogListener) getActivity();
        } catch (ClassCastException e) {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement HostEditDialogListener");
        }
    }

    private int hostid;
    private DatabaseHandler db;

     static fragmentDialogHostEdit newInstance(int hostid) {
        fragmentDialogHostEdit frag = new fragmentDialogHostEdit();

        Bundle bundle = new Bundle();
        bundle.putInt("hostID", hostid);
        frag.setArguments(bundle);
        return frag;
    }










    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //hostid = getArguments().getInt("hostID");
        //Toast.makeText(getActivity(),"dlaskjdfkl",Toast.LENGTH_SHORT).show();

        db = new DatabaseHandler(getActivity());
        final Host host = db.getHost(hostid);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppCompatAlertDialogStyle);

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_dialog_host_entry, null);
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

                        aliasEditText.setText(host._alias, TextView.BufferType.EDITABLE);
                        usernameEditText.setText(host._username, TextView.BufferType.EDITABLE);
                        addressEditText.setText(host._address, TextView.BufferType.EDITABLE);
                        passwordEditText.setText(host._password, TextView.BufferType.EDITABLE);

                        Host newEntry = new Host();
                        newEntry.setAlias(aliasEditText.getText().toString());
                        newEntry.setAddress(addressEditText.getText().toString());
                        newEntry.setUsername(usernameEditText.getText().toString());
                        newEntry.setPassword(passwordEditText.getText().toString());

                        mListener.onHostEditDialogPositiveClick(newEntry);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

return null;
    }

    */

}
