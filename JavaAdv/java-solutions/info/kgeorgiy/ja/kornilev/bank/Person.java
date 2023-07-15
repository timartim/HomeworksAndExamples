package info.kgeorgiy.ja.kornilev.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Person extends Remote {
    String getName() throws RemoteException;
    String getSurname() throws RemoteException;
    String getPassport() throws RemoteException;
    String setName(String newName) throws RemoteException;
    String setSurName(String newSurname) throws RemoteException;
    String setPassport(String newPassport) throws RemoteException;
    public void addMoneyRemotelyOnAccount(String accId, int money) throws RemoteException ;

    void addAccount(Account accountID) throws RemoteException;

    Set<Account> getAccounts()throws RemoteException;
}
