package br.com.jmsstudio.command;

import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;

public class RunEnqueuedCommand implements Runnable {

    private BlockingQueue<String> enqueuedCommands;
    private PrintStream clientOutput;

    public RunEnqueuedCommand(BlockingQueue<String> commands, PrintStream writer) {
        this.enqueuedCommands = commands;
        this.clientOutput = writer;
    }

    @Override
    public void run() {
        String command;

        try {
            while ((command = this.enqueuedCommands.take()) != null) {
                System.out.println("Executing enqueued command");
                this.clientOutput.println("Running enqueued command: " + command);
                Thread.sleep(30000);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
