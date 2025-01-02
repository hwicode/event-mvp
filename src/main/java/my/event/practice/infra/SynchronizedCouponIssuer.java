package my.event.practice.infra;

import my.event.practice.domain.Coupon;
import my.event.practice.domain.CouponIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SynchronizedCouponIssuer implements CouponIssuer {

    private int couponLimit;
    private volatile boolean isClose;

    public SynchronizedCouponIssuer(
            @Value("${coupon.limit:100}")int couponLimit
    ) {
        this.couponLimit = couponLimit;
        isClose = false;
    }

    public synchronized List<Coupon> issue(List<String> memberIds) {
        if (couponLimit == 0) return new ArrayList<>();
        if (couponLimit >= memberIds.size()) {
            couponLimit -= memberIds.size();
            return memberIds.stream()
                    .map(Coupon::new)
                    .toList();
        }

        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < couponLimit; i++) {
            String id = memberIds.get(i);
            coupons.add(new Coupon(id));
        }
        couponLimit = 0;
        isClose = true;
        return coupons;
    }

    // 어느 정도의 시간 오차 허용
    public boolean isClose() {
        return isClose;
    }
}
