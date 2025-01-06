package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponManager {

    private final CouponIssuer couponIssuer;
    private final WaitingQueue waitingQueue;

    public List<Coupon> issueCoupons(List<String> memberIds) {
        List<Coupon> coupons = couponIssuer.issue(memberIds);
        log.info("[CouponManager][issueCoupons] Successfully issue coupons, coupons={}", coupons);

        if (couponIssuer.isClose()) {
            log.info("[CouponManager][issueCoupons] Coupon issuer is closed");

            if (waitingQueue.close()) {
                log.info("[CouponManager][issueCoupons] Closing waiting queue");
            }
        }
        return coupons;
    }
}
