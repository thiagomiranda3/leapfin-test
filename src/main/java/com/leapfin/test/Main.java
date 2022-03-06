package com.leapfin.test;

import com.leapfin.test.stream.DataStream;
import com.leapfin.test.worker.Worker;
import com.leapfin.test.worker.WorkerResult;
import com.leapfin.test.worker.WorkerStatus;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        long timeout = Long.getLong("timeout", 200);
        int numThreads = Integer.getInteger("numThreads", 10);
        String logLevel = System.getProperty("logLevel", "INFO");

        Configurator.setLevel("com.leapfin.test", Level.toLevel(logLevel));

        DataStream stream = new DataStream();
        CountDownLatch latch = new CountDownLatch(numThreads);

        List<CompletableFuture<WorkerResult>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            futures.add(CompletableFuture.supplyAsync(new Worker(stream, latch, i), executor));
        }

        latch.await(timeout, TimeUnit.MILLISECONDS);
        executor.shutdownNow();

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
