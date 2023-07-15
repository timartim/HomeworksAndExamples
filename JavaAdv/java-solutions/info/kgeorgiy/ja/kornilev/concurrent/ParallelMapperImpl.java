package info.kgeorgiy.ja.kornilev.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    int numberOfthreads;
    Thread[] threads;
    Deque<Runnable> deque;
    public ParallelMapperImpl(int numberOfthreads) {
        this.numberOfthreads = numberOfthreads;
        threads = new Thread[numberOfthreads];
        deque = new ArrayDeque<>();
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(() -> {
                synchronized (this){
                    while (deque.isEmpty()){
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Runnable runnable = deque.poll();
                    runnable.run();
                    notify();
                }
            });
            threads[i].start();
        }
    }
    public ParallelMapperImpl(ParallelMapper parallelMapper){}
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        synchronized (this){
            for(T arg : args){
                Runnable runnable = () -> f.apply(arg);
                deque.add(runnable);
            }
        }
        return null;
    }

    @Override
    public void close() {
        for(final Thread thread: threads){
            thread.interrupt();
        }
    }
}
