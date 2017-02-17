package com.example.eduardo.gshell;

/**
 * Created by eduardo on 10/02/17.
 */

public class QueueAdapter {
    public String string;
    public OutputHandler output_handler;

    public QueueAdapter(String cmd, OutputHandler output_handler) {
        this.string = cmd;
        this.output_handler = output_handler;
    }
}
