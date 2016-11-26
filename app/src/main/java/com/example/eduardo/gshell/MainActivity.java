package com.example.eduardo.gshell;

import com.jcraft.jsch.*;
import java.util.Properties;
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
import java.io.ByteArrayOutputStream;
import android.os.AsyncTask;


public class MainActivity extends AppCompatActivity {

    public String output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new AsyncTask<Integer, Void, Void>(){
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    output = executeRemoteCommand("er1414", "imperial10@@@2015","login.cx1.hpc.ic.ac.uk", 22);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);


        // END TEST Server
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView gridView = (ListView)findViewById(R.id.server_list);

        gridView.setAdapter(new ListAdapter(getApplicationContext()));

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
                switch (position) {
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Shop", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //Toast.makeText(getApplicationContext(), "Map", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //Toast.makeText(getApplicationContext(), "Music", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        break;
                }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String executeRemoteCommand(String username,String password,String hostname,int port)
            throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, hostname, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();
            ChannelExec channelssh = (ChannelExec)
                    session.openChannel("exec");
            channelssh.setOutputStream(baos);
            // Execute command
            channelssh.setCommand("ls");
            channelssh.connect();
            while(!channelssh.isClosed())
                Thread.sleep(1000L);
            Log.d("SSH channel status:", String.valueOf(channelssh.getExitStatus()));
            channelssh.disconnect();
            session.disconnect();
        }
        catch (Exception e)
        {
            Log.d("SSH error:", e.getMessage());
        }
        Log.d("Command output:", "EXIT");
        Log.d("Command output:", baos.toString());
        return baos.toString();
    }
}