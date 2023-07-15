package info.kgeorgiy.ja.kornilev.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalPerson implements Person, Serializable {
    String name;
    String surName;
    String passport;
    Map<Account, Integer> accounts;

    public LocalPerson(String name, String surName, String passport) {
        this.name = name;
        this.surName = surName;
        this.passport = passport;
        accounts = new ConcurrentHashMap<>();
    }

   @Override
    public String getName() {
        System.out.println("Getting name for " + name + " " + surName + " " + passport);
        return name;
    }

    @Override
    public String getSurname(){
        System.out.println("Getting surName for " + name + " " + surName + " " + passport);
        return surName;
    }

    @Override
    public String getPassport(){
        System.out.println("Getting passport for " + name + " " + surName + " " + passport);
        return passport;
    }

    @Override
    public String setName(String newName){
        System.out.println("Setting new Name for " + name + " " + surName + " " + passport);
        this.name = newName;
        return newName;
    }

    @Override
    public synchronized String setSurName(String newSurname){
        System.out.println("Setting new SurName for " + name + " " + surName + " " + passport);
        this.surName = newSurname;
        return newSurname;
    }

    @Override
    public synchronized String setPassport(String newPassport){
        System.out.println("Setting new passport for "+ name + " " + surName + " " + passport);
        this.passport = newPassport;
        return newPassport;
    }

    @Override
    public synchronized void addAccount(Account accountID){
        accounts.put(accountID, 0);
    }
    public synchronized void addMoneyRemotelyOnAccount(String accId, int money){
    }
    public synchronized void addLocalMoneyOnAccount(Account account, int money){
        try {
            accounts.put(account, accounts.get(account) + money);
        }catch (Exception e){
            System.out.println("Did not find account, that trying to add money on");
        }
    }
    public synchronized int getLocalMoneyFromAccount(Account account){
        try {
            return accounts.get(account);
        }catch (Exception e){
            System.out.println("Did not find account, that trying to add money on");
        }
        return 0;
    }
    @Override
    public synchronized Set<Account> getAccounts(){
        return accounts.keySet();
    }
}
