package com.example.eduardo.gshell;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class TabFragment2 extends Fragment {

    Server server;
    Handler handler = new Handler();
    int i;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ArrayList<QJob> jobsList;

    public TabFragment2(Server server)
    {
        this.server = server;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        jobsList = new ArrayList<QJob>();

        server.exec_cmd("qstat | wc -l && echo -e \\\\x4", new OutputHandler(handler) {
            @Override
            public void exec(String output) {
               //TextView text2 = (TextView) getView().findViewById(R.id.textView2);
                String jobNamestr;

                String nJobsstr = output.split("\n")[0];

                int nJobs = Integer.parseInt(nJobsstr);
                nJobs = nJobs - 2;
                if (nJobs > 0) {
                    UpdateJobs(nJobs);
                }
            }
        });

        return inflater.inflate(R.layout.tab_fragment_2, container, false);

    }

    public int UpdateJobs(int nJobs) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expandableListView = (ExpandableListView) getView().findViewById(R.id.JobsList);

        for (i = 1; i < nJobs + 1; i++) {
            // Get the job names and use them to extract full data about each job individually
            server.exec_cmd("qstat -f $(qstat | tail -" + String.valueOf(i) + " | tr ' ' '\n' | head -n 1) && echo -e \\\\x4", new OutputHandler(handler) {
                @Override
                public void exec(String output) {

                    output = output.replaceAll("\n\t","");
                    String[] detailsStrArr = output.split("\n");
                    String jobNamestr = detailsStrArr[2].split(" = ")[1];
                    String jobIDstr = detailsStrArr[1].split(": ")[1];
                    QJob tempJob  = new QJob(jobIDstr,jobNamestr);

                    // Adding child data
                    listDataHeader.add(jobNamestr);
                    int thisJob = listDataHeader.size();
                    List<String> jobDetails = new ArrayList<String>();
                    for (int jobDetInd = 1; jobDetInd < detailsStrArr.length; jobDetInd++) {
                        jobDetails.add(detailsStrArr[jobDetInd]);
                        tempJob.addDetail(detailsStrArr[jobDetInd].split(" = ")[0],detailsStrArr[jobDetInd].split(" = ")[1]);
                    }
                    listDataChild.put(listDataHeader.get(thisJob-1), jobDetails); // Header, Child data
                    jobsList.add(tempJob);
                    refreshJobsList();
                }
            });


        }
        return 0;
    }

    public int refreshJobsList() {
        expandableListAdapter = new ExpandableListAdapter(expandableListView.getContext(), listDataHeader, listDataChild, jobsList);
        // setting list adapter
        expandableListView.setAdapter(expandableListAdapter);

        //TextView hLabel = expandableListAdapter.getGroupViewText(0,true,null,null);
        //hLabel.setBackgroundColor(Color.BLUE);
        //hLabel.setTextColor(Color.BLUE);
        return 0;
    }

    private class myTask extends AsyncTask<Integer, Void, Void> {
        String output;
        Server server;


        //initiate vars
        public myTask(Server server) {
            super();
            this.server = server;
            //my params here
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                output = executeRemoteCommand(server.user_name, server.passwd,server.hostname, server.port);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            //do stuff
            showText(output);
        }
    }

    public void showText(String output){
        TextView text = (TextView) getView().findViewById(R.id.textView_tab_fragment_1);
        text.setText(output);
    }

    public static String executeRemoteCommand(String username,String password,String hostname,int port)
            throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedReader consoleOutput = null;
        String output ="";
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
            Channel channelssh = session.openChannel("shell");
            PrintStream commander = new PrintStream(channelssh.getOutputStream(), true);
            channelssh.connect();
            commander.println("ls -l --color=never && echo -e \\\\x4" );
            consoleOutput = new BufferedReader(new InputStreamReader(channelssh.getInputStream()));
            Log.d("SSH channel status:", String.valueOf(channelssh.getExitStatus()));
            String line;
            boolean done = false;
            do  {
                line = consoleOutput.readLine();
                if (line == null)
                    line = "";
                if (!line.equals("\u0004"))
                    output += line + "\n";
                else
                    done = true;
            } while (!done);
            channelssh.disconnect();
            session.disconnect();

        }
        catch (Exception e)
        {
            Log.d("SSH error:", e.getMessage());
        }
        //Log.d("Command output:", baos.toString());

        Log.d("Command output: ", output);
        Log.d("Command output:", "EXIT");
        return output;
    }
}