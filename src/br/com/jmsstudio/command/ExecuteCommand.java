package br.com.jmsstudio.command;

import br.com.jmsstudio.thread.TaskDistributor;

import java.io.PrintStream;
import java.util.Random;

public class ExecuteCommand extends DefaultCommand {

    public ExecuteCommand(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void run() {
        System.out.println("Running command " + TaskDistributor.EXEC_COMMAND);

        if (!new Random().nextBoolean()) {
            throw new RuntimeException("Odd exception");
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.printStream.println(TaskDistributor.EXEC_COMMAND + " command successfully executed");

    }
}
