public class PhilosophersArbitrator {
    public static void main(String args[]) {
        Table table = new Table();
        ArbitratorSemaphore arbitrator = new ArbitratorSemaphore();
        Philosopher[] philosophers = new Philosopher[5];

        for(int i=0; i<5; i++) {
            philosophers[i] = new Philosopher(table, i, arbitrator);
            philosophers[i].start();
        }
    }
}
