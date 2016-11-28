package com.example.eduardo.gshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

public class HostDeleteActivity extends AppCompatActivity {

    public String output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_delete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //--------
        //listing files in directory to pass onto the ListAdapter Method
        final String[] fileArray;
        final File contextDir = getApplicationContext().getFilesDir();
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


        //Log.d("File", "hi");

        //---need to save files in new directory - need to change Server.java accordingly


        ListView gridView = (ListView) findViewById(R.id.server_list);
        final ListAdapter adapter = new ListAdapter(getApplicationContext(), fileArray, imageId);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                removeFileFromDirectory(position, view);
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            /*@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Server s = Server.load(contextDir.getAbsolutePath() + "/dataFiles" + "/"+ adapter.labelId[position]);
                Intent intent = new Intent(getApplicationContext(), qstat_activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SERVER", s);
                intent.putExtras(bundle);
                startActivity(intent);

            }*/


        });

    }

    // method to remove server file from directory
    protected void removeFileFromDirectory(int position, View view) {
        Snackbar.make (view, "Include function to delete file number " + String.valueOf(position), Snackbar.LENGTH_LONG)
                .setDuration(2000).show();
        //new File(contextDir.getAbsolutePath() + "/dataFiles"+"/"+fileArray[position]).delete();
    }
}
