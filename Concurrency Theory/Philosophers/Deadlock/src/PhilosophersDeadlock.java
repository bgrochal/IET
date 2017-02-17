public class PhilosophersDeadlock {

    public static void main(String args[]) {
        Philosopher[] philosophers = new Philosopher[5];
        Table table = new Table();

        for(int i=0; i<5; i++) {
            philosophers[i] = new Philosopher(table, i);
            philosophers[i].start();
        }
    }
}
