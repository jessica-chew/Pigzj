import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {
    private Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public void addTask(Runnable task) {
        tasks.offer(task);
    }

    public Runnable getNextTask() {
        return tasks.poll(); // Retrieves and removes the head of this queue, or returns null if this queue is empty.
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
