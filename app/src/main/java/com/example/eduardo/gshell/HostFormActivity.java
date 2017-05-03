package com.example.eduardo.gshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;

import java.io.File;

/**
 * Created by Hikmat on 24/11/2016.
 */

public class HostFormActivity extends AppCompatActivity{

    protected Server server;
    protected EditText aliasEditText;
    protected EditText usernameEditText;
    protected EditText hostnameEditText;
    protected EditText passwordEditText;
    protected HostFormConnectionTask conn_task;
    protected TextView testConnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_form_activity);
        aliasEditText = (EditText) findViewById(R.id.aliasEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        hostnameEditText = (EditText) findViewById(R.id.hostnameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        testConnTextView = (TextView) findViewById(R.id.testConnectionCaption);

    }

    protected Server getServerFromForms(){
        String alias = aliasEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String hostname = hostnameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        return new Server(alias, username, password, hostname);
    }

    public void saveDetails(View view) {
        server = getServerFromForms();
        String filePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/dataFiles";
        server.save(filePath);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void testConnection(View view){
        server = getServerFromForms();
        conn_task = new HostFormConnectionTask(server, this);
        conn_task.execute();
    }

    public class HostFormConnectionTask extends ConnectionTask {

        public HostFormConnectionTask(Server server, AppCompatActivity activity){
            super(server, activity);
        }

        @Override
        protected void ifConnected() {
            testConnTextView.setTextColor(Color.GREEN);
            testConnTextView.setText("  Success!");
            server.disconnect();
        }

        @Override
        protected void ifNotConnected() {
            testConnTextView.setTextColor(Color.RED);
            testConnTextView.setText("  Failed!");
        }
    }
}
