package my.event.practice.web;

import my.event.practice.domain.EventWaitingService;
import my.event.practice.domain.EventWinnerService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EventWaitingService eventWaitingService;

    @MockBean
    EventWinnerService eventWinnerService;

    private static final String JSESSIONID = "JSESSIONID";

    private MockHttpSession createMockHttpSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("memberId", 1L);
        return session;
    }

    // JSESSIONID은 톰캣에서 관리되므로 테스트 불가, 대신 세션을 모킹해서 테스트 진행함
    @Test
    void 이벤트_참석을_요청하면_201_상태코드가_리턴된다() throws Exception {
        // given
        int waitingOrder = 1;
        MockHttpSession session = createMockHttpSession();

        given(eventWaitingService.participate(anyLong()))
                .willReturn(waitingOrder);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/event/attend")
                        .cookie(new Cookie(JSESSIONID, JSESSIONID))
                        .session(session)
        );

        // then
        perform.andExpect(status().isCreated())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(waitingOrder)
                        )
                );

        verify(eventWaitingService, times(1)).participate(anyLong());
    }

    @Test
    void 이벤트_대기_순서를_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        int waitingOrder = 1;
        MockHttpSession session = createMockHttpSession();

        given(eventWaitingService.getOrder(anyLong()))
                .willReturn(waitingOrder);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/event/waiting-order")
                        .cookie(new Cookie(JSESSIONID, JSESSIONID))
                        .session(session)
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(waitingOrder)
                        )
                );

        verify(eventWaitingService, times(1)).getOrder(anyLong());
    }

    @Test
    void 이벤트_당첨을_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        boolean isWinner = true;
        MockHttpSession session = createMockHttpSession();

        given(eventWinnerService.isEventWinner(anyLong()))
                .willReturn(isWinner);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/event/winner")
                        .cookie(new Cookie(JSESSIONID, JSESSIONID))
                        .session(session)
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(isWinner)
                        )
                );

        verify(eventWinnerService, times(1)).isEventWinner(anyLong());
    }

    @Test
    void 이벤트_참석을_요청할_때_세션이_없다면_401에러가_발생한다() throws Exception {
        // given when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/event/attend")
                        .cookie(new Cookie(JSESSIONID, JSESSIONID))
        );

        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    void 이벤트_참석을_요청할_때_세션에_회원_아이디가_없다면_401에러가_발생한다() throws Exception {
        // given
        int waitingOrder = 1;
        MockHttpSession session = new MockHttpSession();

        given(eventWaitingService.participate(anyLong()))
                .willReturn(waitingOrder);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/event/attend")
                        .cookie(new Cookie(JSESSIONID, JSESSIONID))
                        .session(session)
        );

        // then
        perform.andExpect(status().isUnauthorized());
    }

}
