package com.example.eduardo.gshell;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryo on 16/04/17.
 * Very simple database class based on the android dev site examples.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    //password for encryption
    private static final String password_encrypt = MainActivity.getPassEnryptString();

    // Database Name
    private static final String DATABASE_NAME = "hostsManager";

    // Contacts table name
    private static final String TABLE_HOSTS = "hosts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PASSWORD = "password";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HOSTS_TABLE = "CREATE TABLE " + TABLE_HOSTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ALIAS + " TEXT,"
                + KEY_USERNAME + " TEXT," + KEY_ADDRESS + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_HOSTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOSTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addHost(Host host) {
        SQLiteDatabase db = this.getWritableDatabase(password_encrypt);

        ContentValues values = new ContentValues();
        values.put(KEY_ALIAS, host.getAlias()); // Host alias
        values.put(KEY_USERNAME, host.getUsername()); // Host username
        values.put(KEY_ADDRESS, host.getAddress()); // Host address
        values.put(KEY_PASSWORD, host.getPassword()); // Host password

        // Inserting Row
        db.insert(TABLE_HOSTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single host entry
    Host getHost(int id) {
        //TODO FIX THIS STUFF

        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase(password_encrypt);
        }
        catch(IllegalStateException|SQLiteException e){

            //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            //FragmentManager manager = getSupportFragmentManager();
            //fragmentDialogHostEntry testfrag = new fragmentDialogHostEntry();
            //testfrag.show(manager,"fragment_host_entry");
            return null;
        }
        //catch(Exception e){

        Cursor cursor = db.query(TABLE_HOSTS, new String[] { KEY_ID,
                        KEY_ALIAS, KEY_USERNAME, KEY_ADDRESS, KEY_PASSWORD }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Host host = new Host(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),
                cursor.getString(4));
        // return host entry
        db.close();
        return host;
    }

    // Getting All Hosts
    public List<Host> getAllHosts() {
        List<Host> hostList = new ArrayList<Host>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_HOSTS;

        SQLiteDatabase db = this.getWritableDatabase(password_encrypt);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Host host = new Host();
                host.setID(Integer.parseInt(cursor.getString(0)));
                host.setAlias(cursor.getString(1));
                host.setUsername(cursor.getString(2));
                host.setAddress(cursor.getString(3));
                host.setPassword(cursor.getString(4));

                // Adding host to list
                hostList.add(host);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return hostList;
    }

    // Updating single host entry
    public int updateHost(Host host) {
        SQLiteDatabase db = this.getWritableDatabase(password_encrypt);

        ContentValues values = new ContentValues();
        values.put(KEY_ALIAS, host.getAlias());
        values.put(KEY_USERNAME, host.getUsername());
        values.put(KEY_ADDRESS, host.getAddress());
        values.put(KEY_PASSWORD, host.getPassword());

        // updating row
        return db.update(TABLE_HOSTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(host.getID()) });
    }

    // Deleting single host entry
    public void deleteHost(Host host) {
        SQLiteDatabase db = this.getWritableDatabase(password_encrypt);
        db.delete(TABLE_HOSTS, KEY_ID + " = ?",
                new String[] { String.valueOf(host.getID()) });
        db.close();
    }


    // Getting host count
    public int getHostsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_HOSTS;
        SQLiteDatabase db = this.getReadableDatabase(password_encrypt);
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}
