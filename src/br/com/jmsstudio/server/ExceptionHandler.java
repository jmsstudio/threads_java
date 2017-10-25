package br.com.jmsstudio.server;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("An error ocurred in thread " + t.getName() + ", with the message: " + e.getMessage());
    }
}
