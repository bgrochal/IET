public class Producer implements Runnable {
    private Thread producer;
    private Resource resource;

    public Producer(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        while(true) {
            resource.produce(1);
        }
    }

    public void start() {
        if(producer == null) {
            producer = new Thread(this);
            producer.start();
        }
    }
}
