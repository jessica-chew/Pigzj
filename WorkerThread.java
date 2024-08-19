public class WorkerThread extends Thread {
    private TaskQueue taskQueue;

    public WorkerThread(TaskQueue taskQueue, String threadName) {
        super(threadName);
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (!taskQueue.isEmpty()) {
            Runnable task = taskQueue.getNextTask();
            if (task != null) {
                task.run();
            }
        }
    }
}
