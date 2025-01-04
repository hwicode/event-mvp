package my.event.practice.web.login;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TokenManager tokenManager;

    @Test
    void 로그인을_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        String memberId = "memberId";
        String token = "token";

        mock(TokenManager.class);
        given(tokenManager.createToken(anyString()))
                .willReturn(token);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
                        .queryParam(memberId, memberId)
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(token)
                );

        verify(tokenManager, times(1)).createToken(anyString());
    }

    @Test
    void 회원_아이디없이_로그인을_요청하면_400_상태코드가_리턴된다() throws Exception {
        // given when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
        );

        // then
        perform.andExpect(status().isBadRequest());
    }

}
