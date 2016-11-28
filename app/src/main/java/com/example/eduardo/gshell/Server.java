package com.example.eduardo.gshell; /**
 * Created by eduardo on 24/11/16.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import android.content.Context;
import java.io.FileInputStream;
import android.util.Log;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class Server implements Serializable{

    private static final long serialVersionUID=9023849032938493028L;

    public String name;
    public String user_name;
    public String passwd;
    public String hostname;
    public int port = 22;

    public Server(String name, String user_name, String passwd, String hostname) {
        this.name = name;
        this.user_name = user_name;
        this.passwd = passwd;
        this.hostname = hostname;
    }

    public void save(String filePath) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {

            fos = new FileOutputStream(new File(filePath+"/"+name));
            //fos = context.openFileOutput(filePath+"/"+name, context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();

        }
        catch(Exception e){Log.d("Saving error:", e.getMessage());}

    }

    public static Server load(String filePathPlusName) {
        Server server = null;
        try {
            FileInputStream fis = new FileInputStream(new File(filePathPlusName));
            ObjectInputStream is = new ObjectInputStream(fis);
            server = (Server) is.readObject();
            is.close();
            fis.close();
        }
        catch(Exception e){Log.d("Loading error:", e.getMessage());}
        return server;
    }

    public String toString() {
        return "Server: " + this.name + ", user: " + this.user_name + ", password: " + this.passwd + ", hostname: " + this.hostname;
    }
 }
