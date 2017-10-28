package br.com.jmsstudio.server;

import br.com.jmsstudio.thread.DefaultThreadFactory;
import br.com.jmsstudio.thread.TaskDistributor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTask {

    public static final int SERVER_PORT = 9876;

    private AtomicBoolean isRunning;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private BlockingQueue<String> commandQueue;

    public ServerTask() throws IOException {
        this.isRunning = new AtomicBoolean(true);
        this.serverSocket = new ServerSocket(SERVER_PORT);
        this.threadPool = Executors.newCachedThreadPool(new DefaultThreadFactory());
        this.commandQueue = new ArrayBlockingQueue<>(2);
    }

    public void execute() throws IOException {
        System.out.println("=== Starting server ===");

        while (this.isRunning.get()) {
            Socket socket = this.serverSocket.accept();

            System.out.println("[SERVER] - Accepting new client on port " + socket.getPort());
            System.out.println("[SERVER] - Accepting new client on local port " + socket.getLocalPort());

            TaskDistributor taskDistributor = new TaskDistributor(socket, this, this.threadPool, this.commandQueue);
            this.threadPool.execute(taskDistributor);
        }

        this.serverSocket.close();
    }

    public void stop() throws IOException {
        System.out.println("=== Stopping server ===");
        this.isRunning.set(false);
        this.threadPool.shutdown();
        this.serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        new ServerTask().execute();
    }
}
