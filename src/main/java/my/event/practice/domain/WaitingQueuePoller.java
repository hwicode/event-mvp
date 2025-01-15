package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaitingQueuePoller {

    private final WaitingQueue waitingQueue;

    public List<Long> poll() {
        int repeatLimit = waitingQueue.getRepeatLimit();
        int sleepMs = waitingQueue.getSleepMs();

        // 재시도
        for (int repeatCount = 1; repeatCount <= repeatLimit; repeatCount++) {
            List<Long> memberIds = waitingQueue.pollMemberIds(repeatCount);

            if (!memberIds.isEmpty()) {
                log.info("[WaitingQueuePoller][poll] Success on repeatCount={}, memberIds={}", repeatCount, memberIds);
                return memberIds;
            }

            // 작업을 최대한 일찍 끝내기 위해 빈 리스트를 반환하는 방식 사용
            boolean isInterrupted = sleep(sleepMs);
            if (isInterrupted) break;
        }
        log.info("[WaitingQueuePoller][poll] Polling completed, no members found");
        return Collections.emptyList();
    }

    // 자바에서는 블로킹 메서드들의 내부에서 스레드의 인터럽트 상태를 확인함
    // 그래서 블로킹된 와중에 스레드가 인터럽트 상태로 변하면 InterruptedException 발생
    // InterruptedException 예외가 발생하면 인터럽트 상태가 clear 됨
    private boolean sleep(int sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }

}
