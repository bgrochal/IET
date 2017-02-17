public class MonitoringMain {
    public static void main(String args[]) {
        Resource resource = new Resource();
        for(int i=0; i<(50+Math.random()%100); i++)
            new Consumer(resource).start();
        for(int i=0; i<(50+Math.random()%100); i++)
            new Producer(resource).start();
    }
}
