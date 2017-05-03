package com.example.eduardo.gshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;

//This is the main Ativity

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        fragmentDialogHostEntry.HostEntryDialogListener,
        fragmentDialogHostDelete.HostDeleteDialogListener,
        fragmentDialogHostEditList.HostEditListDialogListener,
        fragmentDialogHostEdit.HostEditDialogListener{

    public String output;
    private String[] fileArray;
    private Integer[] imageId;
    private File contextDir;
    private SwipeLayout swipeLayout;
    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private MainActivityListAdapter adapter;


    public static String password_encrypt = null;

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getIntent().hasExtra("password_encrypt")){
            password_encrypt = getIntent().getExtras().getString("password_encrypt");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase.loadLibs(this);
        DatabaseHandler db = new DatabaseHandler(this);

        //db.addHost(new Host("Tycho","apollo","apollo@tycho.xxx.xxx","password"));
        //db.addHost(new Host("Hex","ceto","ceto@hex.zzz.zzz","password2"));
        //db.addHost(new Host("Zeus","simon","simon@zeus.www.www","password3"));
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new fragmentHostList())
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        listView = (ListView) findViewById(R.id.server_list);
//
//        setCreateFloatingActionButton();
//        getServerListFromDir();
//        setListViewAdapter();

    }
    //--------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (id == R.id.nav_add) {
            fragmentDialogHostEntry testfrag = new fragmentDialogHostEntry();
            testfrag.show(manager,"fragment_host_entry");
            // Handle the camera action
        } else if (id == R.id.nav_edit) {
            fragmentDialogHostEditList testfrag = new fragmentDialogHostEditList();
            testfrag.show(manager,"fragment_host_edit_list");

        } else if (id == R.id.nav_delete) {
            fragmentDialogHostDelete testfrag = new fragmentDialogHostDelete();
            testfrag.show(manager,"fragment_host_delete");

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onHostEntryDialogPositiveClick(Host newEntry) {
        // user touched the dialog's positive button
        DatabaseHandler db = new DatabaseHandler(this);
        db.addHost(newEntry);
        //db.addHost(new Host("asssdss","apollo","apollo@tycho.xxx.xxx","password"));

        // calling the list fragment again so that it updates with the new db
        // kind of messy, probably a cleaner way of doing this
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new fragmentHostList())
                .commit();
    }

    @Override
    public void onHostDeleteDialogPositiveClick(List<Integer> checkedHosts, List<Host> hostData ) {
        DatabaseHandler db = new DatabaseHandler(this);
        //Toast.makeText(this,"clicked delete",Toast.LENGTH_SHORT).show();

        for (Integer id:checkedHosts) {
            Host host = hostData.get(id);
            //Toast.makeText(this,"deleted " + host._alias, Toast.LENGTH_SHORT).show();
            db.deleteHost(host);
        }
        // calling the list fragment again so that it updates with the new db
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new fragmentHostList())
                .commit();
    }

    @Override
    public void onHostEditDialogPositiveClick(Host host){
        DatabaseHandler db = new DatabaseHandler(this);
        db.updateHost(host);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new fragmentHostList())
                .commit();

    }

    @Override
    public void onHostEditListDialogPositiveClick(Host host) {
        FragmentManager manager = getSupportFragmentManager();
        fragmentDialogHostEdit testfrag = fragmentDialogHostEdit.newInstance(host._id);
        testfrag.show(manager,"fragment_host_edit");
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

//    private void setCreateFloatingActionButton(){
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), HostFormActivity.class));
//            }
//        });
//    }

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
                String pwd = getPassEnryptString();
                Server s = Server.load(contextDir.getAbsolutePath() + "/dataFiles" + "/"+ adapter.labelId[position], pwd);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Log.d("File", "settings");
//            return true;
//        }
//
//        if (id == R.id.action_add) {
//            Log.d("File", "Add host");
//            startActivity(new Intent(getApplicationContext(), HostFormActivity.class));
//            return true;
//        }
//
//       /* if (id == R.id.action_delete) {
//            Log.d("File", "Delete host");
//            startActivity(new Intent(getApplicationContext(), HostDeleteActivity.class));
//            return true;
//        }
//        */
//        return super.onOptionsItemSelected(item);
//    }
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

    //For passing passwords to fragments
    //TODO: Get a better way to do this.
    public static String getPassEnryptString(){
        return password_encrypt;
    }

    public void updateAdapter() {
        adapter.notifyDataSetChanged(); //update adapter
    }

}