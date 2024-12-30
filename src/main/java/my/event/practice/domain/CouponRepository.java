package my.event.practice.domain;

import java.util.List;

public interface CouponRepository {

    List<Coupon> saveAll(List<Coupon> coupons);
}
