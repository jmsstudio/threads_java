package br.com.jmsstudio.thread;

import br.com.jmsstudio.command.DefaultCommand;
import br.com.jmsstudio.command.ExecuteCommand;
import br.com.jmsstudio.command.RunCommand;
import br.com.jmsstudio.server.ServerTask;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class TaskDistributor implements Runnable {

    private Socket socket;
    private ServerTask serverTask;
    private ExecutorService threadPool;

    public static final String RUN_COMMAND = "RUN";
    public static final String EXEC_COMMAND = "EXEC";
    public static final String TEST_COMMAND = "TEST";
    public static final String SHUTDOWN_COMMAND = "SHUTDOWN";

    public TaskDistributor(Socket socket, ServerTask serverTask, ExecutorService threadPool) {
        this.socket = socket;
        this.serverTask = serverTask;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {

        System.out.println("Distributing task to socket " + this.socket);
        boolean isReadingData = true;

        try (Scanner scanner = new Scanner(this.socket.getInputStream());
             PrintStream writer = new PrintStream(this.socket.getOutputStream())) {

            while (scanner.hasNextLine() && isReadingData) {
                final String command = scanner.nextLine().toUpperCase();
                System.out.println("Processing command: " + command);

                DefaultCommand commandToBeExecuted = null;

                switch (command) {
                    case EXEC_COMMAND:
                        commandToBeExecuted = new ExecuteCommand(writer);
                        this.threadPool.execute(commandToBeExecuted);
                        break;
                    case RUN_COMMAND:
                        commandToBeExecuted = new RunCommand(writer);
                        this.threadPool.execute(commandToBeExecuted);
                        break;
                    case TEST_COMMAND:
                        writer.println("Running " + TEST_COMMAND);
                        break;
                    case SHUTDOWN_COMMAND:
                        writer.println("Running " + SHUTDOWN_COMMAND);
                        isReadingData = false;
                        this.serverTask.stop();
                        break;
                    default:
                        writer.println("Invalid command. Please try again.");
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isShutdown(String command) {
        return SHUTDOWN_COMMAND.equals(command.toUpperCase());
    }

}
