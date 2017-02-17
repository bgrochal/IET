public class Table {
    private int[] forks = new int[5];

    public Table() {
        for(int i=0; i<5; i++)
            forks[i] = -1;
    }

    public int getForkStatus(int fork) {
        return this.forks[fork];
    }

    public void setOwner(int philosopher, int fork) {
        this.forks[fork] = philosopher;
    }
}
