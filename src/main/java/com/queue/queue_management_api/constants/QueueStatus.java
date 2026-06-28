package com.queue.queue_management_api.constants;

public final class QueueStatus {

    private QueueStatus() {
    }

    public static final int WAITING = 0;
    public static final int SERVING = 1;
    public static final int COMPLETED = 2;
}