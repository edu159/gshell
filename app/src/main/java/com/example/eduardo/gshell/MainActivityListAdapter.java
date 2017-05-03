package com.example.eduardo.gshell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivityListAdapter extends ArrayAdapter<String> {

    private MainActivity activity;
    private List<String> servers;
    public String[] labelId = null;
    private Integer[] imageId = null;


    public MainActivityListAdapter(MainActivity context, int resource, String[] server_names, Integer[] image_id ) {
        super(context, resource, server_names);
        this.labelId = server_names;
        this.imageId = image_id;
        this.activity = context;
        List list = Arrays.asList(server_names);
        this.servers = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

     /*   convertView = layoutInflater.inflate(R.layout.list_row, null); 
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imgv1); 
        TextView textView = (TextView)convertView.findViewById(R.id.tv1); 
        imageView.setBackgroundResource(imageId[position]);  
        return convertView;
     */


        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.server_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position));
        //textView.setText(labelId[position]); 

        //handling buttons event
        holder.btnEdit.setOnClickListener(onEditListener(position, holder, getContext()));
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));

        return convertView;
    }

    private View.OnClickListener onEditListener(final int position, final ViewHolder holder, final Context context) {

        //listing files in directory to pass onto the ListAdapter Method
        final String[] fileArray;
        final File contextDir = context.getApplicationContext().getFilesDir();
        File dataFilesDir = new File(contextDir.getAbsolutePath() + "/dataFiles");
        //create dir to store dataFiles, in case it does not exist
        try {
            //make the new dir: dataFiles
            dataFilesDir.mkdir();
            //create a list of files in the dataFiles dir:

        }
        catch(Exception e){}

        File[] lsDataFilesDir = dataFilesDir.listFiles();
        fileArray = new String[lsDataFilesDir.length];
        Integer[] imageId = new Integer[lsDataFilesDir.length];
        for (int i = 0; i < lsDataFilesDir.length; ++i){
            fileArray[i] = lsDataFilesDir[i].getName();
            imageId[i] = R.drawable.gshell_bin_pic;
            //new File(contextDir.getAbsolutePath() + "/dataFiles"+"/"+fileArray[i]).delete();
            Log.d("File", fileArray[i]);
        }

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showEditDialog(position, holder);
                Log.d("File", "Edit host");
                Intent FormEditIntent = new Intent(context.getApplicationContext(), HostFormEditActivity.class);
                FormEditIntent.putExtra("filepath",contextDir.getAbsolutePath() + "/dataFiles"+"/"+fileArray[position]);
                context.startActivity(FormEditIntent);
            }
        };
    }

    /**
     * Editting confirm dialog
     * @param position
     * @param holder
     */
    private void showEditDialog(final int position, final ViewHolder holder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder.setTitle("EDIT ALIAS");
        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setText(servers.get(position));
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result edit text
                                servers.set(position, input.getText().toString().trim());

                                //notify data set changed
                                holder.swipeLayout.close();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog and show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position, holder);
            }
        };
    }

    private void showDeleteDialog(final int position, final ViewHolder holder){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Are you sure?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //servers.remove(position);
                        activity.removeServer(position);
                        holder.swipeLayout.close();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // create alert dialog and show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private class ViewHolder {
        private TextView name;
        private View btnDelete;
        private View btnEdit;
        private SwipeLayout swipeLayout;

        public ViewHolder(View v) {
            swipeLayout = (SwipeLayout)v.findViewById(R.id.swipe_layout);
            btnDelete = v.findViewById(R.id.delete);
            btnEdit = v.findViewById(R.id.edit_query);
            name = (TextView) v.findViewById(R.id.name);

            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }
    }
}
