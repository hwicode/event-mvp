package my.event.practice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.event.practice.domain.EventWaitingService;
import my.event.practice.domain.EventWinnerService;
import my.event.practice.web.login.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventWaitingService eventWaitingService;
    private final EventWinnerService eventWinnerService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/event/attend")
    public int participateEvent(@LoginUser String memberId) {
        log.info("[EventController][participateEvent] Received request to participate event, memberId={}", memberId);
        int waitingOrder = eventWaitingService.participate(memberId);
        log.info("[EventController][participateEvent] Returning Member waiting order, memberId={}, waitingOrder={}", memberId, waitingOrder);
        return waitingOrder;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/event/waiting-order")
    public int getEventWaitingOrder(@LoginUser String memberId) {
        log.info("[EventController][getEventWaitingOrder] Received request for waiting order, memberId={}", memberId);
        int waitingOrder = eventWaitingService.getOrder(memberId);
        log.info("[EventController][getEventWaitingOrder] Returning Member waiting order, memberId={}, waitingOrder={}", memberId, waitingOrder);
        return waitingOrder;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/event/winner")
    public boolean isEventWinner(@LoginUser String memberId) {
        log.info("[EventController][isEventWinner] Received request for Checking if member is a winner, memberId={}", memberId);
        boolean isEventWinner = eventWinnerService.isEventWinner(memberId);
        log.info("[EventController][isEventWinner] Returning Winner check result, memberId={}, isEventWinner={}", memberId, isEventWinner);
        return isEventWinner;
    }
}
