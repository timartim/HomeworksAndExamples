package info.kgeorgiy.ja.kornilev.bank.tests;

import info.kgeorgiy.ja.kornilev.bank.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class MyTests {
    static Bank bank;
    static final int PORT = 8088;
    final char changeChar = '1';
    @BeforeClass
    public static void bef(){
        try {
            bank = new RemoteBank(PORT);
            LocateRegistry.createRegistry(PORT);
            UnicastRemoteObject.exportObject(bank, PORT);
            Naming.rebind("http://localhost:8088/bank", bank);
        } catch (final IOException e ) {
            System.out.println("Bank is not bound");
        }
    }
    @Test
    public void test1_FindAndCreate() throws RemoteException {
        System.err.println("test1_FindAndCreate: ");
        String name = "1";
        int money = 10;
        int numOfTests = 100;
        for(int i = 0; i < numOfTests; i++){
            Person createdperson = bank.createAccount(name, name, name, name);
            assertNotNull("Could not find a person", bank.findPerson(name, true));
            assertNotNull("Could not create a person",createdperson);
            name += changeChar;
        }
        name = "1";
        for(int i = 0; i < numOfTests; i++){
            assertNotNull("Could not find a person",bank.findPerson(name, true));
        }
        System.out.println("test1_findAndCreate completed sucsessful");
    }

    @Test
    public void test2_systemSingleAccount() throws RemoteException {
        System.err.println("test2_system: ");
        String name = "2";
        int money = 10;
        int numOfTests = 100;
        for(int i = 0; i < numOfTests; i++){
            Person createdperson = bank.createAccount(name, name, name, name);
            createdperson.addMoneyRemotelyOnAccount(name, money);
            name += '1';
        }
        name = "2";
        for(int i = 0; i < numOfTests; i++){
            Person createdperson = bank.findPerson(name, true);
            Set<Account> accs = createdperson.getAccounts();
            for(Account account : accs){
                assertEquals("test2 failed, could not assign money on account", account.getAmount(), money);
            }
            name += '1';
        }
        System.err.println("Test2_system completed sucsessful");
    }
    @Test
    public void test3_systemManyAccount() throws RemoteException {
        System.err.println("test3_system: ");
        String name = "3";
        int accountId = 1000;
        int money = 10;
        int numOfTests = 100;
        int numOfAccs = 10;
        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.createAccount(Integer.toString(accountId), name, name, name);
                createdperson.addMoneyRemotelyOnAccount(Integer.toString(accountId), money);
                accountId++;
                money++;
            }
            name += '1';
        }
        accountId = 1000;
        money = 10;
        name = "3";
        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.findPerson(name, true);
                Set<Account> accounts = createdperson.getAccounts();
                for(Account account : accounts){
                    if(account.getId().equals(Integer.toString(accountId))){
                       assertEquals("Wrong money, incorrect add", money, account.getAmount());
                       break;
                    }
                }
                accountId++;
                money++;
            }
            name += '1';
        }
    }
    @Test
    public void test4_localChangeAndRemote() throws RemoteException{
        System.err.println("starting test4_localPerson");
        System.err.println("test4_localPerson: ");
        String name = "4";
        String accountID = "4.";
        int money = 10;
        int numOfTests = 2;
        int numOfAccs = 2;
        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.createAccount(accountID, name, name, name);
                bank.savePersonAsLocal(createdperson);
                createdperson.addMoneyRemotelyOnAccount(accountID, money);
                accountID += changeChar;
            }
            name += changeChar;
        }
        Set<LocalPerson> localPersonSet = bank.getLocalPersons();
        for(LocalPerson person: localPersonSet){
            for(Account acc: person.getAccounts()){
                person.addLocalMoneyOnAccount(acc, money + money + money);
            }
        }
        name = "4";
        accountID = "4.";
        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.findPerson(name, true);
                Set<Account> accounts = createdperson.getAccounts();
                for(Account account : accounts){
                    if(account.getId().equals(accountID)){
                        assertEquals("Wrong money, incorrect add", money, account.getAmount());
                        break;
                    }
                }
                accountID += changeChar;
            }
            name += changeChar;
        }

    }
    @Test
    public void test5_Local() throws RemoteException{
        System.err.println("starting test5_changeLocalPersonCheckRemote");
        System.err.println("test5_changeLocalPersonCheckRemote: ");
        String name = "5";
        String accountID = "5.";
        int money = 10;
        int numOfTests = 2;
        int numOfAccs = 2;
        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.createAccount(accountID, name, name, name);
                bank.savePersonAsLocal(createdperson);
                createdperson.addMoneyRemotelyOnAccount(accountID, money);
                accountID += changeChar;
            }
            name += changeChar;
        }

        Set<LocalPerson> localPersonSet = bank.getLocalPersons();
        for(LocalPerson person: localPersonSet){
            for(Account acc: person.getAccounts()){
                assertEquals(0, person.getLocalMoneyFromAccount(acc));
            }
        }
    }
    @Test
    public void test6_DoubleCheckLocalPerson() throws RemoteException{
        System.err.println("starting test4_localPerson");
        System.err.println("test4_localPerson: ");
        String name = "6";
        String accountID = "6.";
        int money = 10;
        int numOfTests = 2;
        int numOfAccs = 2;

        for(int i = 0; i < numOfTests; i++){
            for(int j = 0; j < numOfAccs; j++){
                Person createdperson = bank.createAccount(accountID, name, name, name);
                bank.savePersonAsLocal(createdperson);
                createdperson.addMoneyRemotelyOnAccount(accountID, money);
                accountID += changeChar;
            }
            name += changeChar;
        }
        name = "6";
        for(int i = 0; i < numOfTests; i++){
            Person createdperson = bank.findPerson(name, true);
            List<LocalPerson> vers = bank.getPersonsVersions(createdperson);
            for(LocalPerson person : vers){
                for(Account account : person.getAccounts()){
                    person.addLocalMoneyOnAccount(account, 20);
                }
            }
            for(LocalPerson person : vers){
                for(Account account : person.getAccounts()){
                   assertEquals(20, person.getLocalMoneyFromAccount(account));
                }
            }
            name += '1';
        }
        Set<LocalPerson> localPersonSet = bank.getLocalPersons();
        for(LocalPerson person: localPersonSet){
            for(Account acc: person.getAccounts()){
                assertEquals(20, person.getLocalMoneyFromAccount(acc));
            }
        }
    }
    @AfterClass
    public static void tearDown(){
        bank = null;
    }
}