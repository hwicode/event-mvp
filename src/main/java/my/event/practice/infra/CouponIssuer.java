package my.event.practice.infra;

import my.event.practice.domain.Coupon;

import java.util.ArrayList;
import java.util.List;

public class CouponIssuer {

    private int couponLimit;
    private volatile boolean isClose;

    public CouponIssuer(int couponLimit) {
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
