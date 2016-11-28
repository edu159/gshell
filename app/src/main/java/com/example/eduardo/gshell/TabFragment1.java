package com.example.eduardo.gshell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;


public class TabFragment1 extends Fragment {

    Server server;
    public TabFragment1(Server server)
    {
        this.server = server;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Load:", server.toString());
        new TabFragment1.myTask(server).execute(1);
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
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
        Log.d("Command output:", baos.toString());
        Log.d("Command output:", "EXIT");
        return baos.toString();
    }
}