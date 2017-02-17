import java.util.concurrent.locks.*;

public class Resource {
    private int A;
    private Lock lock = new ReentrantLock();
    private Condition producerCondition = lock.newCondition();
    private Condition consumerCondition = lock.newCondition();

    public Resource() {
        this.A = 0;
    }

    public void produce(int number) {
        lock.lock();
        try {
            while(A != 0)
                producerCondition.await();
        }
       catch(InterruptedException exc) {
            exc.printStackTrace();
        }

        System.out.println("Produce.");
        A = number;
        consumerCondition.signal();
        lock.unlock();
    }

    public void consume() {
        lock.lock();
        try {
            while(A == 0)
                consumerCondition.await();
        }
        catch(InterruptedException exc) {
            exc.printStackTrace();
        }

        System.out.println("Consume.");
        A = 0;
        producerCondition.signal();
        lock.unlock();
    }
}
