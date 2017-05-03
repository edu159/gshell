package com.example.eduardo.gshell;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SERVER_TAB:", "CREATE");
        setContentView(R.layout.server_tab_activity);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        this.server = (Server)bundle.getSerializable("SERVER");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.tab_toolbar);
        //setSupportActionBar(toolbar);

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
//                adapter.file_explorer_fragment.update();
//                adapter.job_monitor_fragment.update();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Allocate memmory for tasks
        check_conn_task = new CheckConnectionTask(server);
        conn_task = new ServerTabConnectionTask(server, this);

        // Try to connect
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
        conn_task.stop();
        super.onBackPressed();
    }

    private class CheckConnectionTask extends AsyncTask<Void, Void, Void> {

        private boolean try_connect = false;
        private boolean stop = false;
        private Server server;

        public CheckConnectionTask(Server server){
            this.server = server;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    //Log.d("CHECK CONN:", "Trying...");
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
                conn_task = new ServerTabConnectionTask(server, ServerTabActivity.this);
                conn_task.execute();
            }
            else {
                Log.d("CHECK CONN:", "Stopped.");
            }
        }
    }

    public class ServerTabConnectionTask extends ConnectionTask {

        public ServerTabConnectionTask(Server server, AppCompatActivity activity){
            super(server, activity);
        }

        @Override
        protected void ifConnected() {
            super.ifConnected();
            //TODO: Use fragment manager
            // Update tabs after connecting
            adapter.file_explorer_fragment.update();
            //adapter.job_monitor_fragment.update();
            //adapter.terminal_fragment.update();
            check_conn_task = new ServerTabActivity.CheckConnectionTask(server);
            check_conn_task.execute();

        }

        @Override
        protected void cleanUp(){
            activity.startActivity(new Intent(activity.getApplicationContext(),
                   MainActivity.class));
        }
    }

}