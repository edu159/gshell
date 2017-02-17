package com.example.eduardo.gshell;

import com.jcraft.jsch.*;
import java.util.Properties;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.AdapterView;
import android.util.Log;
import java.io.File;
import java.util.List;
import java.io.ByteArrayOutputStream;
import android.os.AsyncTask;

//This is the main Ativity

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HostFormActivity.class));
            }
        });

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
            imageId[i] = R.drawable.shop;
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Server s = Server.load(contextDir.getAbsolutePath() + "/dataFiles" + "/"+ adapter.labelId[position]);
                Intent intent = new Intent(getApplicationContext(), ServerTabActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SERVER", s);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("File", "settings");
            return true;
        }

        if (id == R.id.action_add) {
            Log.d("File", "Add host");
            startActivity(new Intent(getApplicationContext(), HostFormActivity.class));
            return true;
        }

        if (id == R.id.action_delete) {
            Log.d("File", "Delete host");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}