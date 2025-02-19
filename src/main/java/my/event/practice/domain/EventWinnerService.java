package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventWinnerService {

    private final EventWinnerManager eventWinnerManager;

    public boolean isEventWinner(Long memberId) {
        return eventWinnerManager.isEventWinner(memberId);
    }
}
