package com.example.eduardo.gshell;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryo on 18/04/17.
 */

public class fragmentDialogHostDelete extends DialogFragment {

    private static List<Host> hostData;
    private DatabaseHandler db;

    public interface HostDeleteDialogListener {
        void onHostDeleteDialogPositiveClick(List<Integer> checkedHosts, List<Host> hostData);
    }

    // use this instance of the interface to deliver action events
    HostDeleteDialogListener mListener;

    // override the Fragment.onAttach() method to deliver action events
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //verify that the host activity implements the callback interface
        try {
            // instantiate the listener so we can send events to the host
            mListener = (HostDeleteDialogListener) getActivity();
        } catch (ClassCastException e) {
            // the activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()+
                    " must implement HostDeleteDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        final List<Host> hostData = db.getAllHosts();
        final List<String> hostList = new ArrayList<String>();
        for (Host host : hostData) {
            hostList.add(host.getAlias());
        }
        final String[] hostArray = hostList.toArray(new String[0]);
        // converting List to String Array because the method OnMultiChoiceClickListener
        // requires a CharSequence as input (String is a CharSequence..)
        final List<Integer> checkedHosts = new ArrayList();

        // using the builder class to construct dialog
        // Instantiating an AlertDialog.Builder object with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppCompatAlertDialogStyle);

        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout

        builder.setTitle("Choose Host To Delete")

        .setMultiChoiceItems(hostArray,null,
                new DialogInterface.OnMultiChoiceClickListener(){
                    @Override public void onClick(DialogInterface dialog, int which,
                                                  boolean isChecked){
                        if(isChecked){
                            checkedHosts.add(which);
                        } else if (checkedHosts.contains(which)){
                            checkedHosts.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        mListener.onHostDeleteDialogPositiveClick(checkedHosts,hostData);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();

    }
}
