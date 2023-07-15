package info.kgeorgiy.ja.kornilev.bank;

import java.io.Serializable;

public class RemoteAccount implements Account, Serializable {
    private final String id;
    private int amount;

    public RemoteAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        System.out.println("Getting amount of money for account " + id);
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        System.out.println("Setting amount of money for account " + id);
        this.amount = amount;
        System.out.println("New amount for " + id + "is " + this.amount );
    }
}
