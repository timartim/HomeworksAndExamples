package info.kgeorgiy.ja.kornilev.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public final class Client {
    /** Utility class. */
    private Client() {}

    public static void main(final String... args) throws RemoteException {
        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }

        //final String accountId = args.length >= 1 ? args[0] : "geo";
        String name = args[0];
        String surName = args[1];
        String passport = args[2];
        String accountId = args[3];
        int diff = Integer.parseInt(args[4]);
        Person person = bank.findPerson(passport, true);
        if (person == null) {
            System.out.println("Creating Person");
        } else {
            System.out.println("Account already exists");
        }
        person = bank.createAccount(accountId, name, surName, passport);
        System.out.println();
        System.out.println("Person  passport: " + person.getPassport());
        System.out.println("Person fullName : " + person.getName() + " " + person.getSurname());
        System.out.println("Adding money on account " + accountId + " " + person.getAccounts().size());
        person.addMoneyRemotelyOnAccount(accountId, diff);
        System.out.println("Person accounts: \n");

        for(Account account : person.getAccounts()){
            System.out.println("account№ " + account.getId() + " amount: " + account.getAmount());
        }
//        Set<Account> acc = bank.getAccountByPerson(person, accountId);
//        int idx = 1;
//        for(Account account : acc){
//            System.out.println("account №" + idx + " amount: " + account.getAmount() + "\n");
//        }
    }
}
