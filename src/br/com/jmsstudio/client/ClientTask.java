package br.com.jmsstudio.client;

import br.com.jmsstudio.server.ServerTask;
import br.com.jmsstudio.thread.TaskDistributor;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class ClientTask {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (Socket client = new Socket("localhost", ServerTask.SERVER_PORT);
             PrintStream printStream = new PrintStream(client.getOutputStream())) {
            
            System.out.println("[CLIENT] - Connection with server established");

            Runnable dataSender = () -> {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String command = scanner.nextLine().trim();

                    if (!command.isEmpty() && !TaskDistributor.isShutdown(command)) {
                        printStream.println(command);
                    } else {
                        System.out.println("Finishing client execution");
                        break;
                    }
                }

            };

            Runnable dataReceiver = () -> {
                try (Scanner serverScanner = new Scanner(client.getInputStream())){

                    while (serverScanner.hasNextLine()) {
                        System.out.println("< " + serverScanner.nextLine());
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            Thread threadSender = new Thread(dataSender);
            new Thread(dataReceiver).start();

            threadSender.start();
            threadSender.join();
        }
    }

}
