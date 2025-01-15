package my.event.practice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final WaitingQueuePoller waitingQueuePoller;
    private final CouponManager couponManager;
    private final CouponRepository couponRepository;

    public List<Coupon> issueCoupons() {
        List<Long> memberIds = waitingQueuePoller.poll();
        List<Coupon> coupons = couponManager.issueCoupons(memberIds);
        return couponRepository.saveAll(coupons);
    }

}
