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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Properties;
import android.os.Handler;


public class TabFragment1 extends Fragment {

    Server server;
    Handler handler = new Handler();

    public TabFragment1(){};
    public TabFragment1(Server server)
    {
        this.server = server;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Loaded:", server.toString());
        //new TabFragment1.myTask(server).execute(1);
        server.connect("shell", 5);

        server.exec_cmd("ls -l --color=neve && echo -e \\\\x4", new OutputHandler(handler) {
            @Override
            public void exec(String output) {
                TextView text = (TextView) getView().findViewById(R.id.textView_tab_fragment_1);
                text.setText(output);

            }
        });

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