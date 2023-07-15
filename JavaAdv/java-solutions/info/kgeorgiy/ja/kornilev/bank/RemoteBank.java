package info.kgeorgiy.ja.kornilev.bank;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Person> passportPersons = new ConcurrentHashMap<>();
    private final Set<LocalPerson> localPersons = ConcurrentHashMap.newKeySet();
    private final Set<String> alreadySavedAsLocalPersons = ConcurrentHashMap.newKeySet();
    public final Map<Person, List<LocalPerson>> localPersonVer = new ConcurrentHashMap<>();
    public RemoteBank(final int port) {
        this.port = port;
    }

    //If their is no person, creat one, as same as account. If their is person, adds an account to person.
    @Override
    public Person createAccount(final String id, String name, String surname, String passport) throws RemoteException {
        Person curPerson = findPerson(passport, true);
        final Account account;
        System.out.println("id");
        if(!accounts.containsKey(id)){
            System.out.println("Creating account for " + id);
            account = new RemoteAccount(id);
        }else{
            account = accounts.get(id);
        }
        if (curPerson == null) {
            curPerson = new RemotePerson(name, surname, passport, port);
            passportPersons.put(passport, curPerson);
        }
        curPerson.addAccount(account);
        passportPersons.put(passport, curPerson);
        accounts.put(id, account);
        System.out.println("Creating person " + id + " " + name + " " + surname + " " + passport);
        if (passportPersons.putIfAbsent(passport, curPerson) == null) {
            UnicastRemoteObject.exportObject(curPerson, port);
            UnicastRemoteObject.exportObject(account, port);
            return curPerson;
        } else {
            return passportPersons.get(passport);
        }
    }

    //Creats localPerson by RemoteOne.
    public Person findPerson(String passport, boolean remotePerson) throws RemoteException {
        if (!passportPersons.containsKey(passport)) {
            return null;
        } else {
            Person person = passportPersons.get(passport);
            Person ans;
            if (remotePerson) {
                ans = new RemotePerson(person.getName(), person.getSurname(), person.getPassport(), port);

            } else {
                ans = new LocalPerson(person.getName(), person.getSurname(), person.getPassport());

            }
            for(Account account : person.getAccounts()){
                ans.addAccount(account);
            }
            return ans;
        }
    }

    //adds person to maps if their is no one yet with this passport
    public void addPerson(String passport, String name, String surName, Set<Account> accounts) throws RemoteException {
        if (!passportPersons.containsKey(passport)) {
            Person person = new RemotePerson(name, surName, passport, port);
            for (Account id : accounts) {
                person.addAccount(id);
            }
            passportPersons.put(passport, person);
        }
    }
    public void savePersonAsLocal(Person person) throws RemoteException {
        LocalPerson ans = new LocalPerson(person.getName(), person.getSurname(), person.getPassport());
        for(Account element: person.getAccounts()){
            ans.addAccount(element);
            ans.addLocalMoneyOnAccount(element, element.getAmount());
        }
        localPersonVer.put(person, new ArrayList<>());
        localPersonVer.get(person).add(ans);
        if(alreadySavedAsLocalPersons.contains(person.getPassport())){
            localPersonVer.get(person).add(ans);
            return;
        }
        localPersons.add(ans);
        alreadySavedAsLocalPersons.add(person.getPassport());

    }
    public void addMoneyOnLocalAccountForPerson(LocalPerson person, Account account, int money){
        person.addLocalMoneyOnAccount(account, money);
    }
    public Set<LocalPerson> getLocalPersons(){
        return localPersons;
    }
    public List<LocalPerson> getPersonsVersions(Person person){
        if(localPersonVer.containsKey(person)){
            return localPersonVer.get(person);
        }
        throw new RuntimeException("trying to get versions of unexisting person");
    }
    public Map<String, Person> getRegisteredPersons(){
        return passportPersons;
    }
    @Override
    public Account getAccount(final String id) {
        System.out.println("Retrieving account " + id);
        return accounts.get(id);
    }
}
