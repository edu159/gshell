package com.example.eduardo.gshell;

import android.util.Log;

/**
 * Created by jacek on 07/04/2017.
 */

public class FileExplorerUnknown extends FileExplorerElement
{
    public FileExplorerUnknown(String lsline)
    {
        super(lsline);
        this.type = "unknown";
        this.imageID = R.drawable.unknownfile;
    }

    public void onCLickAction()
    {
        Log.d("FEAdapter","this object is unknown: " + this.name);
    }
}