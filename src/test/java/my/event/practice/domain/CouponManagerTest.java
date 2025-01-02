package my.event.practice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CouponManagerTest {

    @Test
    void 쿠폰_매니저에서_쿠폰을_발급할_수_있다() {
        // given
        String memberId = "memberId";
        CouponIssuer couponIssuer = mock(CouponIssuer.class);
        when(couponIssuer.issue(anyList())).thenReturn(
                List.of(new Coupon(memberId))
        );

        WaitingQueue waitingQueue = mock(WaitingQueue.class);
        CouponManager couponManager = new CouponManager(couponIssuer, waitingQueue);

        // when
        List<Coupon> coupons = couponManager.issueCoupons(List.of(memberId));

        // then
        assertThat(coupons).hasSize(1);
    }

    @Test
    void 쿠폰_매니저에서_쿠폰_발급을_정지할_수_있다() {
        // given
        CouponIssuer couponIssuer = mock(CouponIssuer.class);
        when(couponIssuer.isClose()).thenReturn(true);

        WaitingQueue waitingQueue = mock(WaitingQueue.class);
        CouponManager couponManager = new CouponManager(couponIssuer, waitingQueue);

        // when
        couponManager.issueCoupons(List.of());

        // then
        verify(waitingQueue, times(1)).close();
    }

}
