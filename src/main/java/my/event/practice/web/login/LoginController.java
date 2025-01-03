package my.event.practice.web.login;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final TokenManager tokenManager;

    @GetMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    public String login(@RequestParam String memberId) {
        return tokenManager.createToken(memberId);
    }
}
