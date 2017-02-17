public class Consumer implements Runnable {
    private Thread consumer;
    private Resource resource;

    public Consumer(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        while(true)
            resource.consume();
    }

    public void start() {
        if(consumer == null) {
            consumer = new Thread(this);
            consumer.start();
        }
    }
}
