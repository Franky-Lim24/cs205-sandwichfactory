package model.pool;

public class CircularQueue {
    private volatile Object[] queue;
    private int head;
    private int tail;
    private int size;

    public CircularQueue(int capacity) {
        queue = new Object[capacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == queue.length;
    }

    public synchronized void enqueue(Object item) {
        if (isFull()) {
            throw new RuntimeException("Queue is full");
        }
        queue[tail] = item;
        tail = (tail + 1) % queue.length;
        size++;
    }

    public synchronized Object dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        Object item = (Object) queue[head];
        head = (head + 1) % queue.length;
        size--;
        return item;
    }
}
