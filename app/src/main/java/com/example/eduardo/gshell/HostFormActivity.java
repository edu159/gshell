package com.example.eduardo.gshell;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

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




    }
}
