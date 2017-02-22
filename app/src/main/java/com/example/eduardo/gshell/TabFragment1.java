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

        server.exec_cmd("ls -l --color=neve && echo -e", new OutputHandler(handler) {
            @Override
            public void exec(String output) {
                TextView text = (TextView) getView().findViewById(R.id.textView_tab_fragment_1);
                text.setText(output);

            }
        });

        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }


}