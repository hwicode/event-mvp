package my.event.practice.web;

import lombok.RequiredArgsConstructor;
import my.event.practice.domain.EventWaitingService;
import my.event.practice.domain.EventWinnerService;
import my.event.practice.web.login.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventWaitingService eventWaitingService;
    private final EventWinnerService eventWinnerService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/event/attend")
    public int participateEvent(@LoginUser String memberId) {
        return eventWaitingService.participate(memberId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/event/waiting-order")
    public int getEventWaitingOrder(@LoginUser String memberId) {
        return eventWaitingService.getOrder(memberId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/event/winner")
    public boolean isEventWinner(@LoginUser String memberId) {
        return eventWinnerService.isEventWinner(memberId);
    }
}
