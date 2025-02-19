package my.event.practice.infra;

import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.WaitingQueue;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SynchronizedWaitingQueue implements WaitingQueue {

    private final int repeatLimit;
    private final int pollingSize;
    private final int sleepMs;
    private final LinkedList<Long> queue;
    private final AtomicBoolean isClose;

    public SynchronizedWaitingQueue(
            @Value("${waiting-queue.repeat-limit:3}")int repeatLimit,
            @Value("${waiting-queue.polling-size:100}")int pollingSize,
            @Value("${waiting-queue.sleep-ms:1000}")int sleepMs
    ) {
        this.repeatLimit = repeatLimit;
        this.pollingSize = pollingSize;
        this.sleepMs = sleepMs;
        this.queue = new LinkedList<>();
        isClose = new AtomicBoolean(false);
    }

    public synchronized int add(Long memberId) {
        if (isClose()) {
            throw new CoreException(ErrorType.QUEUE_CLOSED_ERROR);
        }
        queue.offer(memberId);
        log.info("[SynchronizedWaitingQueue][add] Waiting queue add memberId={}, queueSize={}", memberId, queue.size());
        return queue.size();
    }

    // 순서를 정확하게 리턴할 필요가 없으므로 락 안검
    public int getOrder(Long memberId) {
        int order = queue.indexOf(memberId);
        if (isClose() || order == -1) {
            throw new CoreException(ErrorType.MEMBER_NOT_IN_QUEUE_ERROR);
        }
        return order;
    }

    public synchronized List<Long> pollMemberIds(int repeatCount) {
        if (isClose()) {
            return Collections.emptyList();
        }
        if (!isMaxRepeat(repeatCount) && isLowItems()) {
            return Collections.emptyList();
        }
        // 반복 횟수 한계에 도달하면, pollingCount < 큐의 크기인 경우 발생
        int itemsToPoll = Math.min(queue.size(), pollingSize);
        return getMemberIds(itemsToPoll);
    }

    private List<Long> getMemberIds(int itemsToPoll) {
        List<Long> memberIds = new ArrayList<>();
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

    // queue.isEmpty()와 isClose.compareAndSet() 사이에 원자성이 부족함
    // 그래서 isClose를 true로 변경시키지 전에 큐에 값이 추가될 수 있음
    // 그러나 메서드 목적은 적절한 때에 대기 큐를 닫는 것이므로 위의 문제가 상관 없기에 락 X
    public boolean close() {
        if (queue.isEmpty()) {
            return isClose.compareAndSet(false, true);
        }
        return false;
    }

    private boolean isClose() {
        return isClose.get();
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public int getSleepMs() {
        return sleepMs;
    }

}
