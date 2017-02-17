package com.example.eduardo.gshell;

import android.os.Handler;

/**
 * Created by eduardo on 27/01/17.
 */

public class OutputHandler {
    private Handler handler;

    public OutputHandler(Handler handler) {
        this.handler = handler;
    }

    // This method has to be override
    public void exec(String output) {}

    final public void handle_output (final String output) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                exec(output);
            }
        });

    }
}
