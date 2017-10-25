package br.com.jmsstudio.thread;

import br.com.jmsstudio.server.ExceptionHandler;

import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory {

    private static int threadId = 0;

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "Thread_" + threadId);
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(new ExceptionHandler());

        return t;
    }
}
