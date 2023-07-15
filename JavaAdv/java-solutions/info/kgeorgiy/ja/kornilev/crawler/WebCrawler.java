package info.kgeorgiy.ja.kornilev.crawler;

import info.kgeorgiy.java.advanced.crawler.*;
import net.java.quickcheck.collection.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    public static class MyPair<T, M>{
        T first;
        M second;
        MyPair(T first, M second){
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
    Downloader downloader;
    final ExecutorService download;
    final ExecutorService executor;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.download = Executors.newFixedThreadPool(downloaders);
        this.executor = Executors.newFixedThreadPool(extractors);
        this.downloader = downloader;
    }

    @Override
    public Result download(String url, int depth) {
        Map<String, IOException> map = new ConcurrentHashMap<>();
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Set<String> lists = ConcurrentHashMap.newKeySet();
        Phaser phaser = new Phaser(1);
        walk(downloader, depth, visited, map, url, phaser, lists);
        //phaser.arriveAndAwaitAdvance();
        visited.removeAll(map.keySet());
        return new Result(new ArrayList<>(visited), map);
    }

    public void walk(Downloader downloader, int depth, Set<String> visited, Map<String, IOException> errors, String url, Phaser phaser, Set<String> lists) {
        if (depth <= 0) {
            return;
        }
        Queue<MyPair<String, Integer>> queue = new ConcurrentLinkedQueue<>();
        queue.offer(new MyPair<>(url, depth));
        while (!queue.isEmpty()) {
            MyPair<String, Integer> pair = queue.peek();
            queue.poll();
            Phaser finalPhaser = phaser;
            finalPhaser.register();
            download.submit(() -> {
                    String cur = pair.getFirst();
                    visited.add(cur);
                    int deep = pair.getSecond();
                    try {
                        Document document = downloader.download(cur);
                        if (deep > 1) {
                            finalPhaser.register();
                            executor.submit(() -> {
                                try {
                                    for (String link : document.extractLinks()) {
                                        if (visited.add(link)) {
                                            queue.add(new MyPair<>(link, deep - 1));
                                        }
                                    }
                                } catch (IOException e) {
                                    errors.put(url, e);
                                } finally {
                                    finalPhaser.arrive();
                                }
                            });
                        }
                    } catch (IOException e) {
                        errors.put(cur, e);
                        throw new RuntimeException(e);
                    }finally {
                        finalPhaser.arrive();
                    }
                    lists.add(url);
            });
            if(queue.size() == 0){
                finalPhaser.arriveAndAwaitAdvance();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("wrong number of args");
        } else {
            String url = args[0];
            int deph = Integer.parseInt(args[1]);
            int downloads = Integer.parseInt(args[2]);
            int extractors = Integer.parseInt(args[3]);
            int petHost = Integer.parseInt(args[4]);

            CachingDownloader downloader1 = new CachingDownloader(1);
            WebCrawler webCrawler = new WebCrawler(downloader1, downloads, extractors, petHost);
            Result result = webCrawler.download(url, deph);
            System.out.println("downloaded : " + result.getDownloaded().size());
            for (String element : result.getDownloaded()) {
                System.out.println(element);
            }
            System.out.println("error: " + result.getErrors().keySet().size());
            for (String element : result.getErrors().keySet()) {
                System.out.println(element);
            }
        }
    }


    @Override
    public void close() {
        download.shutdown();
        executor.shutdown();
    }
}