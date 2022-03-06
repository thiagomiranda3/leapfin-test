package com.leapfin.worker;

import com.leapfin.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public final class Worker implements Supplier<WorkerResult> {

    private final int workerNumber;
    private final Stream<String> stream;
    private final CountDownLatch latch;
    private final long startTime = System.currentTimeMillis();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private long bytesRead = 0;

    public Worker(Stream<String> stream, CountDownLatch latch, int workerNumber) {
        this.stream = stream;
        this.latch = latch;
        this.workerNumber = workerNumber;
    }

    @Override
    public WorkerResult get() {
        try {
            while (!Thread.interrupted()) {
                String data = stream.getData();

                bytesRead += data.getBytes(StandardCharsets.UTF_8).length;

                if (data.equals("Lpfn")) {
                    return new WorkerResult(getElapsedTime(), bytesRead, WorkerStatus.SUCCESS);
                }
            }
        } catch (Exception e) {
            logger.error("Exception processing worker " + workerNumber, e);
            return new WorkerResult(0, 0, WorkerStatus.FAILURE);
        } finally {
            latch.countDown();
        }

        logger.debug("Timeout while processing worker " + workerNumber);
        return  new WorkerResult(0, 0, WorkerStatus.TIMEOUT);
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}
