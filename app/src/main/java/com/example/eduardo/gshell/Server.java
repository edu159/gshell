package com.example.eduardo.gshell; /**
 * Created by eduardo on 24/11/16.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;


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
    public String error_msg;
    public int timeout = 10000; // In millisecs
    public boolean connected = false;
    private String mode = "shell";
    private PrintStream commander;
    private ReentrantLock connection_lock;

    @Override
    public void run() {
        _poll_cmd();
    }

    public void exec_cmd(String cmd, OutputHandler oh) {
        if (connected) {
            cmd_queue.add(new QueueAdapter(cmd, oh));
        }
    }

    public void shutdown() {
        if (connected) {
            connected = false;
            interrupt();
        }
    }

    private void _poll_cmd() {
        QueueAdapter cmd = null;
        while (true) {
            String output;
            try {
                cmd = cmd_queue.take();
            }
            catch ( InterruptedException ie) {
                Log.d("POLL:", "Polling thread interrupted.");
            }
            if (this.connected) {
                Log.d("POLL:", "PASSED");
                connection_lock.lock();
                try {
                    output = try_send_cmd(cmd.string);
                    cmd.output_handler.handle_output(output);

                }
                catch (IOException e) {
                    Log.d("POLL:", "DISCONNECTED");
                    // If the command fails to be sent then aggressively disconnect
                    ssh_channel.disconnect();
                    ssh_session.disconnect();
                    connected = false;
                }
                finally {
                    connection_lock.unlock();
                    // Thread will exit.
                    if (!connected) {
                        break;
                    }
                }
            }
            else {
                // Thread will exit.
                break;
            }
        }
    }

    public Server(String name, String user_name, String passwd, String hostname) {
        this.name = name;
        this.user_name = user_name;
        this.passwd = passwd;
        this.hostname = hostname;
    }


    public void connect(String mode, int timeout) throws IOException{
        this.timeout = timeout;
        this.connection_lock  = new ReentrantLock();
        this.cmd_queue = new LinkedTransferQueue<QueueAdapter>();
        this.mode = mode;
        try {
            try_connect();
        }
        catch (IOException e) {
            throw new IOException(e);
        }
        // Start polling for commands in the background
        this.start();
    }

    public boolean checkConnState(){
        if (ssh_channel.isClosed() | !ssh_session.isConnected()){
            connected = false;
        }
        return connected;
    }

    private  void try_connect() throws IOException {
        this.jsch = new JSch();
        try {
            this.ssh_session = jsch.getSession(this.user_name, this.hostname, this.port);
            this.ssh_session.setPassword(this.passwd);
            Log.d("Connection info:", this.toString());
            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            this.ssh_session.setConfig(prop);
            ssh_session.setTimeout(5000);
            this.ssh_session.connect(timeout);
            this.ssh_channel = this.ssh_session.openChannel(this.mode);
            this.ssh_channel.connect(timeout);
            this.connected = true;
        }
        catch (JSchException e) {
            Log.d("SSH connection error:", e.getMessage());
            this.connected = false;
            this.error_msg = e.getMessage();
            throw new IOException(e);
        }
    }
    public void disconnect() {
        this.connection_lock.lock();
        this.ssh_channel.disconnect();
        this.ssh_session.disconnect();
        this.connection_lock.unlock();
    }

    private String try_send_cmd(String cmd) throws IOException {
        String output = "";
        BufferedReader consoleOutput;
        try {
            Log.d("SEND:", "BEGIN");
            this.commander = new PrintStream(this.ssh_channel.getOutputStream(), true);
            this.commander.println(cmd + " " + this.CMD_EOF);
            consoleOutput = new BufferedReader(new InputStreamReader(this.ssh_channel.getInputStream()));
            String line;

            boolean done = false;
            long t_ini = System.currentTimeMillis();
            long t_fin;
            // The first line is the command executed. Remove it from the output.
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
                t_fin = System.currentTimeMillis();

                if (Math.abs(t_fin - t_ini) > 10000) {
                    throw new IOException("Send timeout. Connection seems down!");
                }
            } while (!done);
        }
        catch (Exception e){
            Log.d("SEND ERROR:", e.getMessage());
            throw new IOException(e);
        }
        Log.d("SEND:", "END");
        return output;

    }
    //public void send_cmd(String cmd) {}

    public void save (String filePath) {
        FileOutputStream fos;
        ObjectOutputStream os;
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
