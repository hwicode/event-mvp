package my.event.practice.web.login;

import lombok.RequiredArgsConstructor;
import my.event.practice.support.error.CoreException;
import my.event.practice.support.error.ErrorType;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER = "Bearer ";
    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractAccessToken(authorization);
        return tokenManager.getMemberId(token);
    }

    private String extractAccessToken(String authorization) {
        if (authorization == null) {
            throw new CoreException(ErrorType.EMPTY_AUTHORIZATION_HEADER_ERROR);
        }
        return authorization.replace(BEARER, "");
    }
}
