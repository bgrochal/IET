public class ArbitratorSemaphore {
    private int value;

    public ArbitratorSemaphore() {    /* Taken semaphore means that fork is on a table. Released semaphore - that fork is using now. */
        this.value = 4;
    }

    public synchronized void take() throws InterruptedException {
        while(this.value == 4)
            wait();

        this.value++;
        this.notify();
    }

    public synchronized void release() throws InterruptedException {
        while(this.value == 0)
            wait();

        this.value--;
        this.notify();
    }
}
