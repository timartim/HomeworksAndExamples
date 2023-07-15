package info.kgeorgiy.ja.kornilev.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IterativeParallelism implements ScalarIP {
    ParallelMapper parallelMapper;

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    public IterativeParallelism() {

    }

    public <F, R, T> T run(int threads, Function<Stream<? extends F>, R> function, Function<Stream<? extends R>, T> collectAns, List<? extends F> values) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        List<R> ansG = new ArrayList<>(Collections.nCopies(threads, null));
        int actualSize = 0;
        int sublistSize = values.size() / threads;
        int remained = values.size() % threads;
        int idx = 0;
        for (int i = 0; i < threads; i++) {
            int size = sublistSize + (i < remained ? 1 : 0);
            if (values.subList(idx, idx + size).isEmpty()) {
                break;
            }
            actualSize++;
            final int left = idx;
            final int right = idx + size;
            final int j = i;
            threadList.add(new Thread(() ->{
                R ans = function.apply(values.subList(left, right).stream());
                ansG.set(j, ans);
            }));
            threadList.get(i).start();
            idx += size;
        }
        for(int i = 0; i < threadList.size(); i++){
            threadList.get(i).join();
        }

        return collectAns.apply(ansG.subList(0, actualSize).stream());
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return run(threads, stream -> stream.max(comparator).orElse(null), stream -> stream.max(comparator).orElse(null), values);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return run(threads, stream -> stream.min(comparator).orElse(null), stream -> stream.min(comparator).orElse(null), values);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(threads, stream -> stream.allMatch(predicate), stream -> stream.allMatch(Boolean::booleanValue), values);
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(threads, stream -> stream.anyMatch(predicate), stream -> stream.anyMatch(Boolean::booleanValue), values);
    }

    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(threads, stream -> (int) stream.filter(predicate).count(), stream -> stream.mapToInt(Integer::intValue).sum(), values);
    }
}
