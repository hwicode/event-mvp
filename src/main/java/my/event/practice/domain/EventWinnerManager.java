package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EventWinnerManager {

    private final CouponRepository couponRepository;

    public boolean isEventWinner(Long memberId) {
        Optional<Coupon> coupon = couponRepository.findByMemberId(memberId);
        return coupon.isPresent();
    }
}
