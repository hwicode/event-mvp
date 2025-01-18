package my.event.practice.domain;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    List<Coupon> saveAll(List<Coupon> coupons);
    Optional<Coupon> findByMemberId(Long memberId);
    List<Coupon> findAll();
}
