package com.example.eduardo.gshell; /**
 * Created by eduardo on 24/11/16.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import android.content.Context;
import java.io.FileInputStream;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.PrintStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.LinkedTransferQueue;



public class Server extends Thread implements Serializable{

    private static final long serialVersionUID=9023849032938493028L;
    private final String CMD_EOF = "&& echo -e \\\\x4";
    private LinkedTransferQueue<QueueAdapter> cmd_queue;
    private JSch jsch;
    public String name;
    public String user_name;
    public String passwd;
    public String hostname;
    public int port = 22;
    private Session ssh_session;
    private Channel ssh_channel;
    public int timeout = 5;
    private boolean connected = false;
    private String mode = "shell";
    private PrintStream commander;

    @Override
    public void run() {
        try_connect();
        _poll_cmd();

    }

    public void exec_cmd(String cmd, OutputHandler oh) {
        if (this.connected) {
            cmd_queue.add(new QueueAdapter(cmd, oh));
        }
    }

    private void _poll_cmd() {
            while (true) {
                try {
                    String output;
                    Log.d("Polling:", "yeah before");
                    QueueAdapter cmd = this.cmd_queue.take();
                    Log.d("Polling:", "yeah after");
                    output = this.try_send_cmd(cmd.string, cmd.output_handler);
                    cmd.output_handler.handle_output(output);


                }
                catch ( InterruptedException ie ) {
                    // just terminate
                }
            }

    }

    public Server(String name, String user_name, String passwd, String hostname) {
        this.cmd_queue = new LinkedTransferQueue<QueueAdapter>();
        this.name = name;
        this.user_name = user_name;
        this.passwd = passwd;
        this.hostname = hostname;
    }

    public void ping(){}
    public void connect(String mode, int timeout) {
        this.timeout = timeout;
        this.mode = mode;
        this.connected = true;
        this.start();
    }

    private  void try_connect() {
        this.jsch = new JSch();
        try {
            this.ssh_session = jsch.getSession(this.user_name, this.hostname, this.port);
            this.ssh_session.setPassword(this.passwd);
            Log.d("Connection info:", this.toString());
            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            this.ssh_session.setConfig(prop);
            this.ssh_session.connect();
            this.ssh_channel = this.ssh_session.openChannel(this.mode);

            this.ssh_channel.connect();

        }
        catch (Exception e) {
            Log.d("SSH connection error:", e.getMessage());
            this.connected = false;
        }
    }
    public void disconnect(){
        this.ssh_channel.disconnect();
        this.ssh_session.disconnect();
    }

    private String try_send_cmd(String cmd, OutputHandler oh) {
        String output = "";
        BufferedReader consoleOutput;
        try {

            Log.d("cmd", cmd);
            this.commander = new PrintStream(this.ssh_channel.getOutputStream(), true);
            this.commander.println(cmd + " " + this.CMD_EOF);
            consoleOutput = new BufferedReader(new InputStreamReader(this.ssh_channel.getInputStream()));
            Log.d("SSH channel status:", String.valueOf(this.ssh_channel.getExitStatus()));
            String line;

            boolean done = false;
            // The first line is the command we just send. Remove from the output.
            boolean first_line = true;
            do {
                line = consoleOutput.readLine();
                if (line == null)
                    line = "";
                if (!line.equals("\u0004"))
                    if (!first_line)
                        output += line + "\n";
                    else
                        first_line = false;
                else
                    done = true;
            } while (!done);
            Log.d("EXITED: ", "Yes!!!");
        }
        catch (Exception e){
            Log.d("SSH error:", e.getMessage());
        }
        Log.d("OUTPUT: ", output);
        return output;

    }
    public void send_cmd(String cmd) {}

    public void save (String filePath) {
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
