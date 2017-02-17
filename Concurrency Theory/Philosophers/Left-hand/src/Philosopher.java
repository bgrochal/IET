import java.util.Random;

public class Philosopher implements Runnable {
    protected Thread philosopher;
    protected Table table;
    protected int number;

    public Philosopher(Table table, int number) {
        this.table = table;
        this.number = number;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Thinking. */
                table.getForks(number).release();
                table.getForks((number+1)%5).release();
                System.out.println("Philosopher " + number + " is eating.");
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Eating. */
                table.getForks((number + 1)%5).take();    /* Taking reversely to releasing - hierarchy of forks. */
                table.getForks(number).take();
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
