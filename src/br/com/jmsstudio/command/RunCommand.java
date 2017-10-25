package br.com.jmsstudio.command;

import br.com.jmsstudio.thread.TaskDistributor;

import java.io.PrintStream;

public class RunCommand extends DefaultCommand {

    public RunCommand(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void run() {
        System.out.println("Running command " + TaskDistributor.RUN_COMMAND);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.printStream.println(TaskDistributor.RUN_COMMAND + " command successfully executed");
    }
}
