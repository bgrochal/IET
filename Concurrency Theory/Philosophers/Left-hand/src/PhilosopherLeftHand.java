public class PhilosopherLeftHand {
    public static void main(String args[]) {
        Table table = new Table();

        for(int i=0; i<4; i++)
            new Philosopher(table, i).start();
        new LeftyPhilosopher(table, 4).start();
    }
}
