package com.example.eduardo.gshell;

/**
 * Created by jacek on 10/02/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public abstract class FileExplorerElement
{
    public String name;
    public String type;
    public String information;
    public int imageID;
    public Boolean executable;
    public Boolean link;

    public TabFragment1 contextFragment;

    public FileExplorerElement(String lsLine)
    {

        List<String> parameters = new ArrayList<String>(Arrays.asList(lsLine.split("\0")));

//        Log.d("FileExplorerElement", "Recieving a line " + lsLine);
//        Log.d("FileExplorerElement", "Recieved name " + parameters.get(0));
        this.name = parameters.get(0);
        this.information = parameters.get(1);
        determineFlags(this.information);
    }

    public abstract void onCLickAction();

    void determineFlags(String information)
    {
        //Check if executable
        if (information.contains("executable"))
        {
            this.executable = true;
        }
        else
        {
            this.executable = false;
        }

        // Check if link
        // TODO find a better way of treating links than assigning them as directories
        if (information.contains("link"))
        {
            this.link = true;
        }
        else
        {
            this.link = false;
        }
    }

    static String determineType(String information)
    {
        // Check for data type
        if (information.contains("text"))
        {
            return "text";
        }
        else if ((information.contains("directory")) || information.contains("link"))
        {
            return "directory";
        }
        else if ((information.contains("data")) || (information.contains("binary")))
        {
            return "binary";
        }
        else
        {
            return "unknown";
        }
    }
}
