package br.com.jmsstudio.thread;

import br.com.jmsstudio.command.*;
import br.com.jmsstudio.server.ServerTask;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class TaskDistributor implements Runnable {

    private Socket socket;
    private ServerTask serverTask;
    private ExecutorService threadPool;
    private BlockingQueue<String> commandQueue;

    public static final String ENQUEUE_COMMAND = "ENQUEUE";
    public static final String EXEC_COMMAND = "EXEC";
    public static final String FETCH_COMMAND = "FETCH";
    public static final String TEST_COMMAND = "TEST";
    public static final String SHUTDOWN_COMMAND = "SHUTDOWN";

    public TaskDistributor(Socket socket, ServerTask serverTask, ExecutorService threadPool, BlockingQueue<String> commandQueue) {
        this.socket = socket;
        this.serverTask = serverTask;
        this.threadPool = threadPool;
        this.commandQueue = commandQueue;
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
                    case ENQUEUE_COMMAND:
                        this.commandQueue.put(command);
                        System.out.println("Command " + ENQUEUE_COMMAND + " added to the queue");

                        RunEnqueuedCommand enqueuedCommand = new RunEnqueuedCommand(this.commandQueue, writer);
                        this.threadPool.execute(enqueuedCommand);
                        break;
                    case FETCH_COMMAND:

                        FetchWSCommand fetchWSCommand = new FetchWSCommand(writer);
                        FetchDBCommand fetchDBCommand = new FetchDBCommand(writer);

                        Future<String> futureDB = this.threadPool.submit(fetchDBCommand);
                        Future<String> futureWS = this.threadPool.submit(fetchWSCommand);

                        Runnable joinResult = () -> {
                            try {
                                String resultDB = futureDB.get(20, TimeUnit.SECONDS);
                                String resultWS = futureWS.get(20, TimeUnit.SECONDS);

                                writer.println(resultDB);
                                writer.println(resultWS);

                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                futureDB.cancel(true);
                                futureWS.cancel(true);

                                writer.println("An error ocurred while fetch data.");
                            }
                        };
                        new Thread(joinResult).start();

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
