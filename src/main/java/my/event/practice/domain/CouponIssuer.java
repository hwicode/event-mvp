package my.event.practice.domain;

import java.util.List;

public interface CouponIssuer {

    List<Coupon> issue(List<String> memberIds);

    boolean isClose();
}
