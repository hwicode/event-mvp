package my.event.practice.infra;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Time {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
