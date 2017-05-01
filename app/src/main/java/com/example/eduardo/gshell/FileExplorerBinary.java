package com.example.eduardo.gshell;

import android.util.Log;

/**
 * Created by jacek on 07/04/2017.
 */

public class FileExplorerBinary extends FileExplorerElement
{
    public FileExplorerBinary(String lsline)
    {
        super(lsline);
        this.type = "binary";
        this.imageID = R.drawable.binaryfile;
    }

    public void onCLickAction()
    {
        Log.d("FEAdapter","this is binary: " + this.name);
    }
}