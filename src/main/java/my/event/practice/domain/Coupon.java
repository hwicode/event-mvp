package my.event.practice.domain;

public class Coupon {

    private final String memberId;

    public Coupon(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
}
