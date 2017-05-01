package com.example.eduardo.gshell;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by jacek on 07/04/2017.
 */

public class FileExplorerText extends FileExplorerElement
{
    public FileExplorerText(String lsline)
    {
        super(lsline);
        this.type = "text";
        this.imageID = R.drawable.textfile;
    }

    Handler handler = new Handler();

    public void onCLickAction()
    {
        Log.d("FEAdapter","CLicking on " + this.name);

        String serverInput = "cat " + this.name;
        String fileContents;

        this.contextFragment.server.exec_cmd(serverInput, new OutputHandler(handler) {
            @Override
            public void exec(String serverOutput) {
                showTextFileDialog(serverOutput);
            }
        });



    }

    public void showTextFileDialog(String fileContents)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this.contextFragment.getActivity());
        builder1.setTitle(this.name);
        builder1.setMessage(fileContents);
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setPositiveButton(
                "Run",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String serverInputRun = "./" + FileExplorerText.this.name;
                        FileExplorerText.this.contextFragment.server.exec_cmd(serverInputRun, new OutputHandler(handler) {
                            @Override
                            public void exec(String serverOutput) {}
                        });
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}