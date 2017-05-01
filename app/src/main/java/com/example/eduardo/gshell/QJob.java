package com.example.eduardo.gshell;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;


public class QJob {

    public String jobID;
    public String jobName;
    public HashMap<String, String> jobDetails;

    // Constructor
    public QJob(String jid, String jname) {
        jobID = jid;
        jobName = jname;
        jobDetails = new HashMap<String, String>();
    }

    public void addDetail(String key, String value) {
        jobDetails.put(key, value);
    }


}
