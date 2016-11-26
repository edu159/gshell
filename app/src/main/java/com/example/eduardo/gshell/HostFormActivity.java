package com.example.eduardo.gshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;

/**
 * Created by Hikmat on 24/11/2016.
 */

public class HostFormActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_form_activity);
    }

    public void saveDetails(View view) {
        EditText aliasEditText = (EditText) findViewById(R.id.aliasEditText);
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);


        String alias = aliasEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String hostname = hostnameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Server server = new Server(alias, username, password, hostname);
        String filePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/dataFiles";

        server.save(filePath);

        //Server s2 = Server.load(filePath+"/cx1");
        //Log.d("Passed:", s2.toString());
        //onUserLeaveHint();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));




    }
}
