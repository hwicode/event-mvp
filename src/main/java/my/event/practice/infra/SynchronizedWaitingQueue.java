package my.event.practice.infra;

import my.event.practice.domain.WaitingQueue;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SynchronizedWaitingQueue implements WaitingQueue {

    private final int repeatLimit;
    private final int pollingSize;
    private final int sleepMs;
    private final LinkedList<String> queue;
    private final AtomicBoolean isClose;

    public SynchronizedWaitingQueue(int repeatLimit, int pollingSize, int sleepMs) {
        this.repeatLimit = repeatLimit;
        this.pollingSize = pollingSize;
        this.sleepMs = sleepMs;
        this.queue = new LinkedList<>();
        isClose = new AtomicBoolean(false);
    }

    public synchronized int add(String memberId) {
        if (isClose()) {
            throw new RuntimeException();
        }
        queue.offer(memberId);
        return queue.size();
    }

    // 순서를 정확하게 리턴할 필요가 없으므로 락 안검
    public int getOrder(String memberId) {
        int order = queue.indexOf(memberId);
        if (isClose() || order == -1) {
            throw new NoSuchElementException();
        }
        return order;
    }

    public synchronized List<String> pollMemberIds(int repeatCount) {
        if (isClose()) {
            throw new RuntimeException();
        }
        if (!isMaxRepeat(repeatCount) && isLowItems()) {
            return Collections.emptyList();
        }
        // 반복 횟수 한계에 도달하면, pollingCount < 큐의 크기인 경우 발생
        int itemsToPoll = Math.min(queue.size(), pollingSize);
        return getMemberIds(itemsToPoll);
    }

    private List<String> getMemberIds(int itemsToPoll) {
        List<String> memberIds = new ArrayList<>();
        for (int i = 0; i < itemsToPoll; i++) {
            memberIds.add(queue.poll());
        }
        return memberIds;
    }

    private boolean isLowItems() {
        return queue.size() < pollingSize;
    }

    private boolean isMaxRepeat(int repeatCount) {
        return repeatLimit == repeatCount;
    }

    public boolean close() {
        return isClose.compareAndSet(false, true);
    }

    public boolean isClose() {
        return isClose.get();
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public int getSleepMs() {
        return sleepMs;
    }

}
