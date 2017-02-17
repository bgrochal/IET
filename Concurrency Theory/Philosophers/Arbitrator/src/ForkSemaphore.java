public class ForkSemaphore {
    private boolean value;

    public ForkSemaphore() {    /* Taken semaphore means that fork is on a table. Released semaphore - that fork is using now. */
        this.value = true;
    }

    public synchronized void take() throws InterruptedException {
        if(this.value)			/* It is forbidden to take actually taken semaphore. */
            throw new InterruptedException();
        this.value = true;
        this.notify();
    }

    public synchronized void release() throws InterruptedException {
        while(!this.value)
            wait();
        this.value = false;
    }
}
