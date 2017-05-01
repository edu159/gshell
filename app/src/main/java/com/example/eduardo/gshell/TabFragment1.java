package com.example.eduardo.gshell;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import android.os.Handler;
import java.util.Arrays;


public class TabFragment1 extends Fragment {

    private View view;
    private ListView TF1ListView;
    public String serverCommand;

    Server server;
    Handler handler = new Handler();

    public TabFragment1() {};

    public TabFragment1(Server server)
    {
        this.server = server;
        this.serverCommand = "file -0 *";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        TF1ListView = (ListView) view.findViewById(R.id.file_list);

        Log.d("Loaded:", server.toString());
        return view;
    }

    public void update() {
        server.exec_cmd(serverCommand, new OutputHandler(handler) {
            @Override
            public void exec(String serverOutput) {
                showLS(serverOutput);
//                if (serverCommand.contains("file"))
//                {
//                    showLS(serverOutput);
//                }
//                outputLines = serverOutput;
            }
        });
    }

    public void showLS(String output) {
        String[] fullOutputList = output.split("\n");
        //TODO Find out howot separate the command lines from the command output lines.
        List<String> outputList = new ArrayList<String>
                (Arrays.asList(Arrays.copyOfRange(fullOutputList, 1, fullOutputList.length)));
        int nfiles = outputList.size();

        List<FileExplorerElement> fileList = new ArrayList<FileExplorerElement>();

        Log.d("FEAdapter", "Registered ssh output. the number of files is " + nfiles);

        //Add a return button
        fileList.add(new FileExplorerGoBack());

        for (int i = 0; i < nfiles; ++i) {
            String name = FileExplorerElement.determineType(outputList.get(i));
            switch (name) {
                case "text":
                    fileList.add(new FileExplorerText(outputList.get(i)));
                    break;
                case "directory":
                    fileList.add(new FileExplorerDirectory(outputList.get(i)));
                    break;
                case "binary":
                    fileList.add( new FileExplorerBinary(outputList.get(i)));
                    break;
                case "unknown":
                    fileList.add( new FileExplorerUnknown(outputList.get(i)));
                    break;
            }
        }
        nfiles ++;

        final FileExplorerListAdapter adapter = new FileExplorerListAdapter(getActivity().getApplicationContext(), fileList);
        TF1ListView.setAdapter(adapter);

        TF1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long lng) {
                FileExplorerElement feItem = adapter.getItem(position);
                Log.d("FileExplorerAdapter", "Clicked at " + feItem.name);
                feItem.contextFragment = TabFragment1.this;
                feItem.onCLickAction();

//                Intent intent = new Intent(TabFragment1.this,destinationActivity.class);
                //based on item add info to intent
//                startActivity(intent);
            }
        });

    }
}