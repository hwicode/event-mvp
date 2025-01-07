package my.event.practice.infra;

import my.event.practice.domain.CouponIssueService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class ScheduledWorkerTest {

    @MockBean
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
