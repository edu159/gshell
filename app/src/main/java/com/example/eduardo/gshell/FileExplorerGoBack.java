package com.example.eduardo.gshell;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by jacek on 07/04/2017.
 */

public class FileExplorerGoBack extends FileExplorerElement
{
    public FileExplorerGoBack()
    {
        super("Go back \0 Essentially go back a level");
        this.type = "return";
        this.imageID = R.drawable.back_arrow;
    }

    public void onCLickAction()
    {
        Log.d("FEAdapter","this object is a return object: " + this.name);
        Log.d("FEAdapter","Clicking on " + this.name);

        this.contextFragment.serverCommand = "cd .. && file -0 *";

        final FragmentTransaction ft = contextFragment.getFragmentManager().beginTransaction();
        ft.detach(contextFragment);
        ft.attach(contextFragment);
        ft.commit();
    }
}