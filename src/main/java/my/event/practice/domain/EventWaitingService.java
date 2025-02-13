package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventWaitingService {

    private final DuplicateChecker duplicateChecker;
    private final WaitingQueue waitingQueue;

    public int participate(Long memberId) {
        duplicateChecker.check(memberId);
        return waitingQueue.add(memberId);
    }

    public int getOrder(Long memberId) {
        return waitingQueue.getOrder(memberId);
    }

}
