package my.event.practice.domain;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventWinnerManagerTest {

    @Test
    void 이벤트_당첨_관리자는_이벤트_당첨자를_알_수_있다() {
        // given
        String memberId = "memberId";
        Coupon coupon = new Coupon(memberId);
        CouponRepository couponRepository = mock(CouponRepository.class);
        when(couponRepository.findByMemberId(anyString())).thenReturn(
                Optional.of(coupon)
        );

        EventWinnerManager eventWinnerManager = new EventWinnerManager(couponRepository);

        // when
        boolean isEventWinner = eventWinnerManager.isEventWinner(memberId);

        // then
        assertThat(isEventWinner).isTrue();
    }

    @Test
    void 이벤트_당첨_관리자는_이벤트_비당첨자를_알_수_있다() {
        // given
        String memberId = "memberId";
        CouponRepository couponRepository = mock(CouponRepository.class);
        when(couponRepository.findByMemberId(anyString())).thenReturn(
                Optional.empty()
        );

        EventWinnerManager eventWinnerManager = new EventWinnerManager(couponRepository);

        // when
        boolean isEventWinner = eventWinnerManager.isEventWinner(memberId);

        // then
        assertThat(isEventWinner).isFalse();
    }

}
