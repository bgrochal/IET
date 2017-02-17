import java.util.Random;

public class LeftyPhilosopher extends Philosopher {
    public LeftyPhilosopher(Table table, int number) {
        super(table, number);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Thinking. */
                table.getForks((number+1)%5).release();     /* Reversely according to Philosopher class. */
                table.getForks(number).release();
                System.out.println("Philosopher " + number + " is eating.");
                Thread.sleep(Math.abs(new Random().nextInt())%2500);   /* Eating. */
                table.getForks(number).take();
                table.getForks((number + 1)%5).take();      /* Taking reversely to releasing - hierarchy of forks. */
            }
            catch(InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }
}
