package model.pool;

public class SandwichPool {
    private final Object[] queue;
    private volatile int size;
    private volatile int front;
    private volatile int end;

    public SandwichPool(int capacity) {
        queue = new Object[capacity];
        front = 0;
        end = 0;
        size = 0;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized boolean isFull() {
        return size == queue.length;
    }

    // This synchronized method implements a blocking queue enqueue operation that waits until there
    // is available space in the queue before adding the item to the end of the queue.
    // Once the item is added, it updates the end index and size, and wakes up any waiting threads using notifyAll().
    public synchronized void enqueue(Object item) {
        while (isFull()) { // while buffer is full
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        queue[end] = item;
        end = (end + 1) % queue.length;
        size += 1;
        this.notifyAll();
    }

    // This synchronized method implements a blocking queue dequeue operation that waits for an item to be
    // added to the queue if empty by another thread. It retrieves the item from the front of the queue,
    // updates the front index and size, and returns the item, ensuring thread-safety.
    public synchronized Object dequeue() {
        while (isEmpty()) { // while buffer empty
            try {
                this.wait(); // we wait for call put().
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        Object item = queue[front];
        front = (front + 1) % queue.length;
        size -= 1;
        return item;
    }
}
