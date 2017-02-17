public class Philosopher implements Runnable {
    private Thread philosopher;
    private Table table;
    private int number;

    public Philosopher(Table table, int number) {
        this.table = table;
        this.number = number;
    }

    @Override
    public void run() {
        System.out.println("Philosopher " + number + " put up fork " + number + ".");
        table.setOwner(number, number);

        try {
            philosopher.sleep(1000);
        }
        catch(InterruptedException exc) {
            exc.printStackTrace();
        }

        while(true) {
            if(table.getForkStatus((number+1)%5) == -1) {
                System.out.println("Philosopher " + number + " put up fork " + (number+1)%5 + ".");
                table.setOwner(number, (number + 1) % 5);
            }
            /*
            else
                System.out.println("Philosopher " + number + " is waiting.");
            */
        }
    }

    public void start() {
        if(philosopher == null) {
            philosopher = new Thread(this);
            philosopher.start();
        }
    }
}
