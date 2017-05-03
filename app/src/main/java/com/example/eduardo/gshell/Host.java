package com.example.eduardo.gshell;

/**
 * Created by ryo on 14/04/17.
 * Simmple data structure for passing around details of a saved host.
 */

public class Host {

    // private varialbes
    int _id;
    String _alias;
    String _address;
    String _username;
    String _password;

    // empty constructor
    public Host(){

    }

    // constructor
    public Host(int id, String alias, String address, String username, String password){
        this._id = id;
        this._alias = alias;
        this._address = address;
        this._username = username;
        this._password = password;
    }

    // constructor
    public Host(String alias, String address, String username, String password){
        this._alias = alias;
        this._address = address;
        this._username = username;
        this._password = password;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting ID
    public void setID(int id){
        this._id = id;
    }

    // getting username
    public String getUsername(){
        return this._username;
    }

    // setting username
    public void setUsername(String username){
        this._username = username;
    }

    // getting alias
    public String getAlias(){
        return this._alias;
    }

    // setting alias
    public void setAlias(String alias){
        this._alias = alias;
    }

    // getting address
    public String getAddress(){
        return this._address;
    }

    public void setAddress(String address){
        this._address = address;
    }

    // getting password
    public String getPassword(){
        return this._password;
    }

    // setting password
    public void setPassword(String password){
        this._password = password;
    }


}
