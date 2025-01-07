package my.event.practice.domain;

import java.util.Objects;

public class Coupon {

    private final String memberId;

    public Coupon(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(memberId, coupon.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
