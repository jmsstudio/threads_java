package br.com.jmsstudio.command;

import java.io.PrintStream;
import java.util.concurrent.Callable;

public class FetchDBCommand implements Callable<String> {

    private PrintStream printStream;

    public FetchDBCommand(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public String call() throws Exception {
        System.out.println("Fetching data from database");

        this.printStream.println("Querying database...");

        Thread.sleep(10000);

        System.out.println("Finished fetching data from database");

        String result = "no records found in db";

        this.printStream.println("Query executed with success");


        return result;
    }
}
