package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class WaitingQueuePoller {

    private final WaitingQueue waitingQueue;

    public List<String> poll() {
        int repeatLimit = waitingQueue.getRepeatLimit();
        int sleepMs = waitingQueue.getSleepMs();

        List<String> memberIds;

        // 재시도
        for (int repeatCount = 1; repeatCount <= repeatLimit; repeatCount++) {
            memberIds = waitingQueue.pollMemberIds(repeatCount);
            if (!memberIds.isEmpty()) {
                return memberIds;
            }
            sleep(sleepMs);
        }
        return Collections.emptyList();
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
