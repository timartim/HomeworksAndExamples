package info.kgeorgiy.ja.kornilev.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemotePerson extends UnicastRemoteObject implements Person, Serializable {
    String name;
    String surName;
    String passport;
    Set<Account> accounts;
    int port;
    public RemotePerson(String name, String surName, String passport, int port) throws RemoteException {
        super(port);
        this.port = port;
        this.name = name;
        this.surName = surName;
        this.passport = passport;
        accounts = ConcurrentHashMap.newKeySet();
    }

    @Override
    public String getName() throws RemoteException {
        System.out.println("Getting name for " + name + " " + surName + " " + passport);
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        System.out.println("Getting surName for " + name + " " + surName + " " + passport);
        return surName;
    }

    @Override
    public String getPassport() throws RemoteException {
        System.out.println("Getting passport for " + name + " " + surName + " " + passport);
        return passport;
    }

    @Override
    public String setName(String newName) throws RemoteException {
        System.out.println("Setting new Name for " + name + " " + surName + " " + passport);
        this.name = newName;
        return newName;
    }

    @Override
    public synchronized String setSurName(String newSurname) throws RemoteException {
        System.out.println("Setting new SurName for " + name + " " + surName + " " + passport);
        this.surName = newSurname;
        return newSurname;
    }

    @Override
    public synchronized String setPassport(String newPassport) throws RemoteException {
        System.out.println("Setting new passport for "+ name + " " + surName + " " + passport);
        this.passport = newPassport;
        return newPassport;
    }

    @Override
    public synchronized void addAccount(Account accountID) throws RemoteException {
        System.out.println("Adding account " + accountID.getId() + " for " + passport + " size " + accounts.size() );
        System.out.println("Money on account : " + accountID.getAmount());
        accounts.add(accountID);
        System.out.println("Added account for " + passport + " size " + accounts.size() );

    }
    public synchronized void addMoneyRemotelyOnAccount(String accId, int money) throws RemoteException {
        Set<Account> acc = this.getAccounts();
        for(Account account : acc){
            System.out.println("Currently comparing " + account.getId() + " " + accId + " " + accounts.size());
            if(account.getId().equals(accId)){
                int prevmoney = account.getAmount();
                System.out.println("Account " + accId + "prevMoney: " + prevmoney + " addMoney" + money);
                account.setAmount(prevmoney + money);
                return;
            }
        }
    }
    @Override
    public synchronized Set<Account> getAccounts() throws RemoteException {
        return accounts;
    }
}
