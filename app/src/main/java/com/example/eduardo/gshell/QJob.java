package com.example.eduardo.gshell;

import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;


public class QJob {

    public String jobID;
    public String jobName;
    public HashMap<String, String> jobDetails;
    public Button button;
    public TabFragment2 tf2;

    // Constructor
    public QJob(String jid, String jname) {
        jobID = jid;
        jobName = jname;
        jobDetails = new HashMap<String, String>();
        button = null;
        tf2 = null;
    }

    public void addDetail(String key, String value) {
        jobDetails.put(key, value);
    }

    public void toggleHold(Server server) {
        Handler handler = new Handler();
        String jobState = this.jobDetails.get("job_state");
        if (jobState.contains((CharSequence) "Q")) {
            server.exec_cmd("qalter -hu " + this.jobID, new OutputHandler(handler) {
                @Override
                public void exec(String output) {
                    // Do you want to do anything with the output?
                }
            });
            this.jobDetails.replace("job_state","Q","H");
        } else if (jobState.contains((CharSequence) "H")) {
            server.exec_cmd("qalter -hn " + this.jobID, new OutputHandler(handler) {
                @Override
                public void exec(String output) {
                    // Do you want to do anything with the output?
                }
            });
            this.jobDetails.replace("job_state","H","Q");
        }
        this.button.setText(this.jobDetails.get("job_state"));
    }


}
