package com.example.eduardo.gshell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import android.support.design.widget.TabLayout.Tab;



public class ServerTabActivity extends AppCompatActivity {

    private Server server;
    private Tab file_explorer_tab;
    private Tab process_monitor_tab;
    private Tab terminal_tab;
    private PagerAdapter adapter;
    private ConnectionTask conn_task;
    private CheckConnectionTask check_conn_task;
    String myLog = "myLog";


    //private file_explorer_tab
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SERVER_TAB:", "CREATE");
        setContentView(R.layout.server_tab_activity);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        this.server = (Server)bundle.getSerializable("SERVER");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tab_toolbar);
        setSupportActionBar(toolbar);

        // Create tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        file_explorer_tab = tabLayout.newTab().setText("Explorer");
        tabLayout.addTab(file_explorer_tab);
        process_monitor_tab = tabLayout.newTab().setText("Job monitor");
        tabLayout.addTab(process_monitor_tab);
        terminal_tab = tabLayout.newTab().setText("Terminal");
        tabLayout.addTab(terminal_tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), server);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                adapter.file_explorer_fragment.update();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        conn_task = new ConnectionTask();
        conn_task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d("BACK PRESSED:", "Cleaning...");
        server.shutdown();
        check_conn_task.stop();
        super.onBackPressed();
        this.finish();
    }

   private class CheckConnectionTask extends AsyncTask <Void, Void, Void> {

        private boolean try_connect = false;
        private boolean stop = false;
        @Override
        protected Void doInBackground(Void... params) {
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    Log.d("CHECK CONN:", "Trying...");
                    if (!server.checkConnState() & !stop) {
                        Log.d("CHECK CONN:", "Disconnected!");
                        try_connect = true;
                        break;
                    }

                }
                catch (InterruptedException e) {}
            }
            return null;
        }

        public void stop(){
            stop = true;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (try_connect) {
                Log.d("CHECK CONN:", "Try reconnecting...");
                conn_task = new ConnectionTask();
                conn_task.execute();
            }
            else {
                Log.d("CHECK CONN:", "Stopped.");
            }
        }
    }

    private class ConnectionTask extends AsyncTask <Void, Void, Void> {
        private AlertDialog alertDialog;
        private AlphaAnimation inAnimation;
        private AlphaAnimation outAnimation;
        private FrameLayout progressBarHolder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            // Create Dialog in case connection went wrong
            alertDialog = new AlertDialog.Builder(ServerTabActivity.this).create();
            alertDialog.setTitle("ERROR");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CLOSE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            server.shutdown();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (!server.connected) {
                alertDialog.setMessage("Connection problem: " + server.error_msg);
                alertDialog.show();
            }
            else {
                // Update tabs after connecting
                adapter.file_explorer_fragment.update();
                //adapter.job_monitor_fragment.update();
                //adapter.terminal_fragment.update();
                check_conn_task = new CheckConnectionTask();
                check_conn_task.execute();
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d("TRY CONN:", "Connecting...");
                server.connect("shell", 10000);

            }
            catch (IOException e) {
                Log.d("TRY CONN:", "Failed to connect!.");
            }

            return null;
        }
    }
}