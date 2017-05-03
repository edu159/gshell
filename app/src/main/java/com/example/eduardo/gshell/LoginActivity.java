package com.example.eduardo.gshell;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity { //implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    //private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        //mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
       // populateAutoComplete();
        final File contextDir = getApplicationContext().getFilesDir();
        File dataFilesDir = new File(contextDir.getAbsolutePath() + "/PasswordFile");
        //create dir to store dataFiles, in case it does not exist
        try {
            //make the new dir: dataFiles
            dataFilesDir.mkdir();
            //create a list of files in the dataFiles dir:

        }
        catch(Exception e){}

        File[] lsDataFilesDir = dataFilesDir.listFiles();
        int nr_files = lsDataFilesDir.length;
        if (nr_files==0) {
            setContentView(R.layout.first_activity_login);
            final Boolean is_new = Boolean.TRUE;
            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin(is_new);
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(is_new);
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
            //TO DO: WARNING IF MORE THAN ONE FILE
        } else {
            setContentView(R.layout.activity_login);
            final Boolean is_new = Boolean.FALSE;
            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin(is_new);
                        return true;
                    }
                    return false;
                }
            });
            String dummy = "There exists an account";
            int toast_dur = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(),dummy,toast_dur);

            toast.show();
            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(is_new);
                }
            });

            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            String dummy = "Deleting all accounts";
                            int toast_dur = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(getApplicationContext(),dummy,toast_dur);

                            toast.show();

                            File dataFilesDir = new File(contextDir.getAbsolutePath() + "/PasswordFile");

                            File[] lsDataFilesDir = dataFilesDir.listFiles();
                            for (int i = 0; i < lsDataFilesDir.length; ++i){
                                // deleting any old app passwords
                                new File(contextDir.getAbsolutePath() + "/PasswordFile"+"/"+lsDataFilesDir[i].getName()).delete();
                            }

                            getApplicationContext().deleteDatabase("hostsManager");

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            Button deleteAccountButton = (Button) findViewById(R.id.delte_all_sign_in_button);
            deleteAccountButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("This is a hard reset and all server files will be deleted. Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }

    }

    private void attemptLogin(Boolean is_new) {

        String filepath = getApplicationContext().getFilesDir().getAbsolutePath() + "/PasswordFile";
        String password_encrypt = mPasswordView.getText().toString();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        if (is_new) {
            //password = password + "_saved";
            Server password_server = new Server("password_file.txt","dummy",password_encrypt, "dummy");
            password_server.save(getApplicationContext().getFilesDir().getAbsolutePath() + "/PasswordFile",password_encrypt);
            intent.putExtra("password_encrypt", password_encrypt);
            startActivity(intent);
            finish();
        } else {
            Server password_server = Server.load(getApplicationContext().getFilesDir().getAbsolutePath() + "/PasswordFile/password_file.txt",password_encrypt);
            int toast_dur = Toast.LENGTH_LONG;
            if (password_encrypt.equals(password_server.passwd)) {

                Toast toast = Toast.makeText(getApplicationContext(),"Password is correct!",toast_dur);

                toast.show();
                intent.putExtra("password_encrypt", password_encrypt);
                startActivity(intent);
                finish();
            }else{
                mPasswordView.setText("");
                Toast toast = Toast.makeText(getApplicationContext(),"Password is incorrect!",toast_dur);
                toast.show();
                return;
            }

        }

        finish();

    }


}

