package com.example.eduardo.gshell;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

/**
 * Created by MArtin on 24/11/2016.
 */

public class HostFormEditActivity extends HostFormActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String filePath = getIntent().getExtras().getString("filepath");
        //Server server = Server.load(filePath);
        aliasEditText.setText(server.name, TextView.BufferType.EDITABLE);
        usernameEditText.setText(server.user_name, TextView.BufferType.EDITABLE);
        hostnameEditText.setText(server.hostname, TextView.BufferType.EDITABLE);
        passwordEditText.setText(server.passwd, TextView.BufferType.EDITABLE);
    }

    @Override
    public void saveDetails(View view) {
        String filePath_old = getIntent().getExtras().getString("filepath");
        new File(filePath_old).delete();
        super.saveDetails(null);
    }

}