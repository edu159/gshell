package com.example.eduardo.gshell;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by jacek on 07/04/2017.
 */

public class FileExplorerDirectory extends FileExplorerElement
{
    public FileExplorerDirectory(String lsline)
    {
        super(lsline);
        this.type = "directory";
        this.imageID = R.drawable.directory;
    }

    public void onCLickAction()
    {
        Log.d("FEAdapter","Clicking on " + this.name);

        this.contextFragment.serverCommand = "cd " + this.name + " && file -0 *";

        final FragmentTransaction ft = contextFragment.getFragmentManager().beginTransaction();
        ft.detach(contextFragment);
        ft.attach(contextFragment);
        ft.commit();
    }
}