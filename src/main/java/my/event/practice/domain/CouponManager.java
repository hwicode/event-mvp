package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CouponManager {

    private final CouponIssuer couponIssuer;
    private final WaitingQueue waitingQueue;

    public List<Coupon> issueCoupons(List<String> memberId) {
        List<Coupon> coupons = couponIssuer.issue(memberId);
        if (couponIssuer.isClose()) {
            waitingQueue.close();
        }
        return coupons;
    }
}
