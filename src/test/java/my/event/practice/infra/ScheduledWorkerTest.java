package my.event.practice.infra;

import my.event.practice.domain.CouponIssueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ScheduledWorkerTest {

    @MockitoBean
    CouponIssueService couponIssueService;

    @Test
    void 스케쥴러는_대략_1초쯤에_쿠폰_발행을_시작한다() {
        // given
        given(couponIssueService.issueCoupons())
                .willReturn(List.of());

        // when then
        await().atMost(1500, TimeUnit.MILLISECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() ->
                        verify(couponIssueService, times(1)).issueCoupons()
                );
    }
}
