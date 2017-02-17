public class Table {
    private ForkSemaphore[] forks = new ForkSemaphore[5];

    public Table() {
        for(int i=0; i<5; i++)
            forks[i] = new ForkSemaphore();
    }

    public ForkSemaphore getForks(int fork) {
        return this.forks[fork];
    }
}
