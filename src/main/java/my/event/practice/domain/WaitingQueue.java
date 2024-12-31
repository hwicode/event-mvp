package my.event.practice.domain;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WaitingQueue {

    private final int repeatCount;
    private final int pollingSize;
    private final LinkedList<String> queue;
    private final AtomicBoolean isClose;

    public WaitingQueue(int repeatCount, int pollingSize) {
        this.repeatCount = repeatCount;
        this.pollingSize = pollingSize;
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
        if (!canPoll(repeatCount)) {
            return Collections.emptyList();
        }
        // 반복 횟수 한계에 도달하면, pollingCount < 큐의 크기인 경우 발생
        int itemsToPoll = Math.min(queue.size(), pollingSize);

        List<String> memberIds = new ArrayList<>();
        for (int i = 0; i < itemsToPoll; i++) {
            memberIds.add(queue.poll());
        }
        return memberIds;
    }

    // 일정 횟수 이상 시도하면 갯수가 적어도 가져감 OR 큐의 크기가 충분히 커야함
    private boolean canPoll(int repeatCount) {
        return repeatCount == this.repeatCount || queue.size() >= pollingSize;
    }

    public boolean close() {
        return isClose.compareAndSet(false, true);
    }

    public boolean isClose() {
        return isClose.get();
    }

}
