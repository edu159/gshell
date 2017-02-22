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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;

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
import java.util.Arrays;


public class TabFragment1 extends Fragment {

    private View view;
    private ListView TF1ListView;
    private String outputLines;

    Server server;
    Handler handler = new Handler();

    public TabFragment1(){};
    public TabFragment1(Server server)
    {
        this.server = server;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        TF1ListView = (ListView) view.findViewById(R.id.file_list);

        Log.d("Loaded:", server.toString());
        //new TabFragment1.myTask(server).execute(1);

        server.exec_cmd("file -0 *", new OutputHandler(handler) {
            @Override
            public void exec(String output) {

                showLS(output);
//                TextView text = (TextView) getView().findViewById(R.id.textView_tab_fragment_1);
//                text.setText(output);

            }
        });

        return view;
    }
    public void showLS(String output)
    {
        String [] fullOutputList = output.split("\n");
        String [] outputList = Arrays.copyOfRange(fullOutputList, 1, fullOutputList.length);
        int nfiles = outputList.length;

        FileExplorerElement [] fileList = new FileExplorerElement[nfiles];

        Log.d("FEAdapter", "Registered ssh output. the number of files is " + nfiles);

        for (int i = 0; i < nfiles; ++i)
        {
            String name = FileExplorerElement.determineType(outputList[i]);
            switch(name)
            {
                case "text":
                    fileList[i] = new FileExplorerText(outputList[i]);
                    break;
                case "directory":
                    fileList[i] = new FileExplorerDirectory(outputList[i]);
                    break;
                case "binary":
                    fileList[i] = new FileExplorerBinary(outputList[i]);
                    break;
                case "unknown":
                    fileList[i] = new FileExplorerUnknown(outputList[i]);
                    break;
            }
        }

        FileExplorerListAdapter adapter = new FileExplorerListAdapter(getActivity().getApplicationContext(), fileList);
        TF1ListView.setAdapter(adapter);
//        TF1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//
//            }
//        }

    }

}