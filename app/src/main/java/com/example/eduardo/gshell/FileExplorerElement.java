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

public abstract class FileExplorerElement
{
    public String name;
    public String type;
    public String information;
    public int imageID;
    public Boolean executable;
    public Boolean link;

    public FileExplorerElement(String lsLine)
    {

        String [] parameters = lsLine.split("\0");

//        Log.d("FileExplorerElement", "Recieving a line " + lsLine);
//        Log.d("FileExplorerElement", "Recieved name " + parameters[0]);
        this.name = parameters[0];
        this.information = parameters[1];
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


class FileExplorerText extends FileExplorerElement
{
    public FileExplorerText(String lsline)
    {
        super(lsline);
        this.type = "text";
        this.imageID = R.drawable.textfile;
    }

    public void onCLickAction()
    {
        System.out.println("less " + this.name);
    }
}

class FileExplorerDirectory extends FileExplorerElement
{
    public FileExplorerDirectory(String lsline)
    {
        super(lsline);
        this.type = "directory";
        this.imageID = R.drawable.directory;
    }

    public void onCLickAction()
    {
        System.out.println("cd " + this.name);
    }
}

class FileExplorerBinary extends FileExplorerElement
{
    public FileExplorerBinary(String lsline)
    {
        super(lsline);
        this.type = "binary";
        this.imageID = R.drawable.binaryfile;
    }

    public void onCLickAction()
    {
        System.out.println("this is binary: " + this.name);
    }
}

class FileExplorerUnknown extends FileExplorerElement
{
    public FileExplorerUnknown(String lsline)
    {
        super(lsline);
        this.type = "unknown";
        this.imageID = R.drawable.unknownfile;
    }

    public void onCLickAction()
    {
        System.out.println("this object is unknown: " + this.name);
    }
}
