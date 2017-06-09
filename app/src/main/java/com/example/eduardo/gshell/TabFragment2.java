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
    int nJobs;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ArrayList<QJob> jobsList;

    public TabFragment2(Server server) {
        this.server = server;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        jobsList = new ArrayList<QJob>();
        return inflater.inflate(R.layout.tab_fragment_2, container, false);

    }

    public void update(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expandableListView = (ExpandableListView) getView().findViewById(R.id.JobsList);

        // Run a command to get the new jobs
        server.exec_cmd("qstat -f", new OutputHandler(handler) {
            @Override
            public void exec(String output) {
                // Condense lines which have been split
                output = output.replaceAll("\n\t", "");

                String[] jobDetailsArray = output.split("\n\n");
                nJobs = jobDetailsArray.length;
                for (int jobInd = 0; jobInd < nJobs; jobInd++) {
                    String[] detailsStrArr = jobDetailsArray[jobInd].split("\n");
                    String jobNamestr = detailsStrArr[1].split(" = ")[1];
                    String jobIDstr = detailsStrArr[0].split(": ")[1];
                    QJob tempJob = new QJob(jobIDstr, jobNamestr);

                    // Adding child data
                    listDataHeader.add(jobNamestr);
                    int thisJob = listDataHeader.size();
                    List<String> jobDetails = new ArrayList<String>();
                    for (int jobDetInd = 2; jobDetInd < detailsStrArr.length; jobDetInd++) {
                        jobDetails.add(detailsStrArr[jobDetInd].trim());
                        String tempdetail1 = detailsStrArr[jobDetInd].split(" = ")[0].trim();
                        String tempdetail2 = detailsStrArr[jobDetInd].split(" = ")[1].trim();
                        tempJob.addDetail(tempdetail1, tempdetail2);
                    }
                    listDataChild.put(listDataHeader.get(thisJob - 1), jobDetails); // Header, Child data
                    jobsList.add(tempJob);
                }

                refreshJobsList();
            }
        });
    }

    public int refreshJobsList() {
        expandableListAdapter = new ExpandableListAdapter(expandableListView.getContext(), listDataHeader, listDataChild, jobsList);

        // setting list adapter
        expandableListView.setAdapter(expandableListAdapter);

        int Hold = 0;
        int Queued = 0;
        int Running = 0;
        int Error = 0;

        TextView nQueuedTV = (TextView) getView().findViewById(R.id.QNum);
        TextView nRunningTV = (TextView) getView().findViewById(R.id.RNum);

        for (int i = 0; i < nJobs; i++) {
            String tempstate = jobsList.get(i).jobDetails.get("job_state");
            if (tempstate.contains("Q")) {
                Queued++;
            } else if (tempstate.contains("R")) {
                Running++;
            } else if (tempstate.contains("H")) {
                Hold++;
            } else if (tempstate.contains("E")) {
                Error++;
            }
        }

        nQueuedTV.setText(String.valueOf(Queued));
        nRunningTV.setText(String.valueOf(Running));

        return 0;
    }
}
