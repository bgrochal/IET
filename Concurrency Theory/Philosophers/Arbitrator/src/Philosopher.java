import java.util.Random;

public class Philosopher implements Runnable {
    private ArbitratorSemaphore arbitrator;
    private Thread philosopher;
    private Table table;
    private int number;

    public Philosopher(Table table, int number, ArbitratorSemaphore arbitrator) {
        this.table = table;
        this.number = number;
        this.arbitrator = arbitrator;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Thinking. */
                arbitrator.release();
                table.getForks(number).release();
                table.getForks((number+1)%5).release();
                System.out.println("Philosopher " + number + " is eating.");
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Eating. */
                table.getForks(number).take();
                table.getForks((number + 1) % 5).take();
                arbitrator.take();
            }
            catch(InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

    public void start() {
        if(philosopher == null) {
            philosopher = new Thread(this);
            philosopher.start();
        }
    }
}
