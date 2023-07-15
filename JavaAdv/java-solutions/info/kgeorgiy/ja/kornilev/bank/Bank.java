package info.kgeorgiy.ja.kornilev.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param id account id
     * @return created or existing account.
     */
    Person createAccount(String id, String name, String surName, String passport) throws RemoteException;
    void addPerson(String passport, String name, String surName, Set<Account> accounts) throws RemoteException;
    void savePersonAsLocal(Person person) throws RemoteException ;
    public void addMoneyOnLocalAccountForPerson(LocalPerson person, Account account, int money);
    public Set<LocalPerson> getLocalPersons();
    public List<LocalPerson> getPersonsVersions(Person person);
    Person findPerson(String passport, boolean remotePerson) throws RemoteException ;

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Account getAccount(String id) throws RemoteException;
}
