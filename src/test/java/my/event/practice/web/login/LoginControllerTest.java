package my.event.practice.web.login;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TokenManager tokenManager;

    @Test
    void 로그인을_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        Long memberId = 1L;
        String token = "token";

        mock(TokenManager.class);
        given(tokenManager.createToken(anyLong()))
                .willReturn(token);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
                        .queryParam("memberId", String.valueOf(memberId))
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(token)
                );

        verify(tokenManager, times(1)).createToken(anyLong());
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
