package com.leapfin.test.worker;

public final class WorkerResult {
    private final long elapsedTime;
    private final long bytesRead;
    private final WorkerStatus status;

    public WorkerResult(long elapsedTime, long bytesRead, WorkerStatus status) {
        this.elapsedTime = elapsedTime;
        this.bytesRead = bytesRead;
        this.status = status;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return elapsedTime + "\t\t" + bytesRead + "\t\t" + status;
    }
}
