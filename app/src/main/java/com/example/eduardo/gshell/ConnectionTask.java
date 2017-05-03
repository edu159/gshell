package com.example.eduardo.gshell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import java.io.IOException;

/**
 * Created by eduardo on 01/05/17.
 */
public class ConnectionTask extends AsyncTask<Void, Void, Void> {
    protected AlertDialog alertDialog;
    protected AlphaAnimation inAnimation;
    protected AlphaAnimation outAnimation;
    protected FrameLayout progressBarHolder;
    protected boolean stop = false;
    protected Server server;
    protected AppCompatActivity activity;


    public ConnectionTask(Server server, AppCompatActivity activity){
        this.server = server;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBarHolder = (FrameLayout) activity.findViewById(R.id.progressBarHolder);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Create Dialog in case connection went wrong
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        alertDialog = builder.create();
        builder.setCancelable(false);
        alertDialog.setTitle("ERROR");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        server.shutdown();
                        cleanUp();
                    }
                });
    }

    public void stop(){
        stop = true;
    }

    protected void ifNotConnected(){}
    protected  void ifConnected(){}
    protected void cleanUp(){}

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // Only if the app has not stopped. CAUTION: This is not the best solution.
        if (!stop) {
            if (!server.connected) {
                ifNotConnected();
                alertDialog.setMessage("Connection problem: " + server.error_msg);
                alertDialog.show();
            }
            else {
                ifConnected();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        try {
            Log.d("TRY CONN:", "Connecting...");
            server.connect("shell", 10000);
        }
        catch (IOException e) {
            Log.d("TRY CONN:", "Failed to connect!.");
        }
        return null;
    }
}
