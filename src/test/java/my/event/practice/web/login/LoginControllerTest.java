package my.event.practice.web.login;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    // JSESSIONID은 톰캣에서 관리되므로 테스트 불가, 대신 세션 속성을 통해 테스트 진행
    @Test
    void 로그인을_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        Long memberId = 1L;
        String memberIdString = "memberId";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/login")
                        .queryParam(memberIdString, String.valueOf(memberId))
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.request().sessionAttribute(memberIdString, memberId)
                );
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
