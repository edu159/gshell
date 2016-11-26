package com.example.eduardo.gshell;
import java.util.HashMap;
import java.lang.String;
import android.content.Context;
import java.io.File;
import android.util.Log;

/**
 * Created by eduardo on 26/11/16.
 */

public class ServerList {
    private HashMap <String, Server> server_list;
    private String path;

    public void ServerList(String path) {
        this.path = path;
    }

    public void addServer(Server s){
        server_list.put(s.name, s);
        //s.save(this.path);
    }

    public void removeServer(String name) {
        try {
            File server_file = new File(this.path + "/" + name);
            server_file.delete();
        }catch(Exception e) {
            Log.d("Server remove error", e.getMessage());
        }
    }


    public Server getServer(String name) {
        return server_list.get(name);
    }

    public void loadServerAll() {

    }
}
