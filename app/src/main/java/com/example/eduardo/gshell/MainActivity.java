package com.example.eduardo.gshell;

import com.daimajia.swipe.SwipeLayout;
import com.jcraft.jsch.*;
import java.util.Properties;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import android.widget.TextView;

//This is the main Ativity

public class MainActivity extends AppCompatActivity {

    public String output;
    private String[] fileArray;
    private Integer[] imageId;
    private File contextDir;
    private SwipeLayout swipeLayout;
    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private MainActivityListAdapter adapter;

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.server_list);

        setCreateFloatingActionButton();
        getServerListFromDir();
        setListViewAdapter();

    }


//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    private void setCreateFloatingActionButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HostFormActivity.class));
            }
        });
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    public void getServerListFromDir(){
        //--------
        //listing files in directory to pass onto the ListAdapter Method
        contextDir = getApplicationContext().getFilesDir();
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
        imageId = new Integer[lsDataFilesDir.length];
        for (int i = 0; i < lsDataFilesDir.length; ++i){
            fileArray[i] = lsDataFilesDir[i].getName();
            imageId[i] = R.drawable.shop;
            //new File(contextDir.getAbsolutePath() + "/dataFiles"+"/"+fileArray[i]).delete();
            Log.d("File", fileArray[i]);
        }
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    private void setSwipeViewFeatures() {
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Log.i(TAG, "onClose");
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.i(TAG, "on swiping");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i(TAG, "on start open");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally show");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally close");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    private void setListViewAdapter(){
        adapter = new MainActivityListAdapter(this, R.layout.server_listview, fileArray, imageId);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

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

       /* if (id == R.id.action_delete) {
            Log.d("File", "Delete host");
            startActivity(new Intent(getApplicationContext(), HostDeleteActivity.class));
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    public void removeServer(int position) {
        //getServerListFromDir();
        File fileToBeDeleted = new File(contextDir.getAbsolutePath() + "/dataFiles" + "/"+ adapter.labelId[position]);
        fileToBeDeleted.delete();
        getServerListFromDir();
        setListViewAdapter();
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------


    public void updateAdapter() {
        adapter.notifyDataSetChanged(); //update adapter
    }

}