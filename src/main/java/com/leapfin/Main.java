package com.leapfin;

import com.leapfin.stream.DataStream;
import com.leapfin.worker.Worker;
import com.leapfin.worker.WorkerResult;
import com.leapfin.worker.WorkerStatus;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Set<String> allowedLevels = new HashSet<>(Arrays.asList("ERROR", "WARN", "INFO", "DEBUG"));

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        long timeout = Long.getLong("timeout", 60000);
        int numThreads = Integer.getInteger("numThreads", 10);
        String logLevel = System.getProperty("logLevel", "INFO");

        if (!allowedLevels.contains(logLevel)) {
            logger.error("Log level not allowed");
            return;
        }

        Configurator.setLevel("com.leapfin", Level.toLevel(logLevel));

        DataStream stream = new DataStream();

        // Class that control the execution of all workers
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Here I create all workers
        List<CompletableFuture<WorkerResult>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            futures.add(CompletableFuture.supplyAsync(new Worker(stream, latch, i), executor));
        }

        // Wait here until timeout or until execution of all threads finishes
        latch.await(timeout, TimeUnit.MILLISECONDS);
        executor.shutdownNow();

        // After finish, get all future results and sort by elapsedTime
        List<WorkerResult> results = futures.stream()
                                            .map(CompletableFuture::join)
                                            .sorted(Comparator.comparing(WorkerResult::getElapsedTime).reversed())
                                            .collect(Collectors.toList());

        printResults(results);
    }

    private static void printResults(List<WorkerResult> results) {
        long totalElapsedTime = 0, totalBytesRead = 0;

        logger.info("Elapsed Time, Bytes Read, Status");
        for (WorkerResult result : results) {
            if (result.getStatus() == WorkerStatus.SUCCESS) {
                totalBytesRead += result.getBytesRead();
                totalElapsedTime += result.getElapsedTime();
            }

            logger.info(result.toString());
        }

        long bytesPerMilli = totalBytesRead > 0 ? totalBytesRead / totalElapsedTime : 0;
        logger.info("Bytes per milli: " + bytesPerMilli);
    }
}
