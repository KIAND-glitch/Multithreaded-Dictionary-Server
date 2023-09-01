/*
 * Kian Dsouza - 1142463
 * Custom Thread Pool
 * */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomThreadPool {
    private final int nThreads;
    private final List<Thread> threads;
    private final LinkedList<Runnable> tasks;

    public CustomThreadPool(int nThreads) {
        this.nThreads = nThreads;
        this.threads = new ArrayList<>(nThreads);
        this.tasks = new LinkedList<>();

        for (int i = 0; i < nThreads; i++) {
            Thread thread = new WorkerThread();
            thread.start();
            threads.add(thread);
        }
    }

    public void submitTask(Runnable task) {
        synchronized (tasks) {
            tasks.addLast(task);
            tasks.notify();
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            Runnable task;
            while (true) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    task = tasks.removeFirst();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                }
            }
        }
    }
}
