package my.event.practice.web;

import my.event.practice.domain.EventWaitingService;
import my.event.practice.domain.EventWinnerService;
import my.event.practice.web.login.TokenManager;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EventWaitingService eventWaitingService;

    @MockitoBean
    EventWinnerService eventWinnerService;

    @MockitoBean
    TokenManager tokenManager;

    private static final String BEARER = "bearer ";

    @Test
    void 이벤트_참석을_요청하면_201_상태코드가_리턴된다() throws Exception {
        // given
        String memberId = "memberId";
        String token = "token";
        int waitingOrder = 1;

        given(tokenManager.getMemberId(anyString()))
                .willReturn(memberId);

        given(eventWaitingService.participate(anyString()))
                .willReturn(waitingOrder);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/event/attend")
                        .header("Authorization", BEARER + token)
        );

        // then
        perform.andExpect(status().isCreated())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(waitingOrder)
                        )
                );

        verify(tokenManager, times(1)).getMemberId(anyString());
        verify(eventWaitingService, times(1)).participate(anyString());
    }

    @Test
    void 이벤트_대기_순서를_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        String memberId = "memberId";
        String token = "token";
        int waitingOrder = 1;

        given(tokenManager.getMemberId(anyString()))
                .willReturn(memberId);

        given(eventWaitingService.getOrder(anyString()))
                .willReturn(waitingOrder);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/event/waiting-order")
                        .header("Authorization", BEARER + token)
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(waitingOrder)
                        )
                );

        verify(tokenManager, times(1)).getMemberId(anyString());
        verify(eventWaitingService, times(1)).getOrder(anyString());
    }

    @Test
    void 이벤트_당첨을_요청하면_200_상태코드가_리턴된다() throws Exception {
        // given
        String memberId = "memberId";
        String token = "token";
        boolean isWinner = true;

        given(tokenManager.getMemberId(anyString()))
                .willReturn(memberId);

        given(eventWinnerService.isEventWinner(anyString()))
                .willReturn(isWinner);

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/event/winner")
                        .header("Authorization", BEARER + token)
        );

        // then
        perform.andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(
                                String.valueOf(isWinner)
                        )
                );

        verify(tokenManager, times(1)).getMemberId(anyString());
        verify(eventWinnerService, times(1)).isEventWinner(anyString());
    }

}
