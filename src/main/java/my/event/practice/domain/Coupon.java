package my.event.practice.domain;

import java.util.Objects;

public class Coupon {

    private final Long memberId;

    public Coupon(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
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

    @Override
    public String toString() {
        return "Coupon[memberId=" + this.memberId + "]";
    }
}
