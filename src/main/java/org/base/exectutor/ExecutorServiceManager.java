package org.base.exectutor;

import org.base.dto.RecordData;

import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceManager {
    public static final int MAX_THREADS = 1000;
    private final ExecutorService executorService;

    public ExecutorServiceManager(int maxThreads) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public <T> CompletableFuture<T> submitTask(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    public void waitForCompletion(List<CompletableFuture<List<RecordData>>> futures) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get(); // Wait for all futures to complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    // This method can be used to create manual Threads for fetching data
    /* private static List<RecordData> fetchRecordDataUsingThreads(List<String> urls, Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors) {
        List<RecordData> allData = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(urls.size());

        System.out.println("Threads before processing: " + Thread.activeCount());

        for (String url : urls) {
            Thread thread = new Thread(() -> {
                try {
                    List<RecordData> data = fetchDataFromUrl(url, urlErrors, lineErrors);
                    allData.addAll(data);
                } finally {
                    latch.countDown(); // Signal that the thread has finished its task
                    System.out.println("Threads after task completion: " + Thread.activeCount());
                }
            });
            thread.start();
        }

        try {
            latch.await(); // Wait for all threads to finish
            System.out.println("All threads have finished processing.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Threads after processing: " + Thread.activeCount());

        return allData.stream()
                .sorted(Comparator.comparing(RecordData::getAge))
                .collect(Collectors.toList());
    } */
}