package info.kgeorgiy.ja.kornilev.i18n;

public class MyPair<T, M> {
    T first;
    M second;
    public MyPair(T first, M second){
        this.first = first;
        this.second = second;
    }
    public T getFirst(){
        return first;
    }
    public M getSecond(){
        return second;
    }
}
