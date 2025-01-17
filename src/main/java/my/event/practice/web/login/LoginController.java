package my.event.practice.web.login;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final HttpSession session;

    @GetMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public void login(@RequestParam Long memberId) {
        session.setAttribute("memberId", memberId);
    }
}
