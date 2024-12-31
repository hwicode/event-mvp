package my.event.practice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssuerTest {

    private static CouponIssuer createCouponIssuer(int couponLimit) {
        return new CouponIssuer(couponLimit);
    }

    private static List<Coupon> createCoupons(List<String> memberIds) {
        return memberIds.stream()
                .map(Coupon::new)
                .toList();
    }

    @Test
    void 쿠폰_발급기는_쿠폰을_발급할_수_있다() {
        // given
        int couponLimit = 3;
        CouponIssuer couponIssuer = createCouponIssuer(couponLimit);

        String memberId1 = "memberId1";
        String memberId2 = "memberId2";
        String memberId3 = "memberId3";
        List<String> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        List<Coupon> expectedCoupons = createCoupons(memberIds);

        // when
        List<Coupon> coupons = couponIssuer.issue(memberIds);

        // then
        assertThat(coupons).containsAll(expectedCoupons);
    }

    @Test
    void 쿠폰_수량이_전부_소진되면_빈_리스트가_리턴된다() {
        // given
        int couponLimit = 0;
        CouponIssuer couponIssuer = createCouponIssuer(couponLimit);

        String memberId1 = "memberId1";
        String memberId2 = "memberId2";
        String memberId3 = "memberId3";
        List<String> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        // when
        List<Coupon> coupons = couponIssuer.issue(memberIds);

        // then
        assertThat(coupons).isEmpty();
    }

    @Test
    void 쿠폰_수량보다_회원_수가_많으면_쿠폰_수량_만큼만_쿠폰을_발행한다() {
        // given
        int couponLimit = 2;
        CouponIssuer couponIssuer = createCouponIssuer(couponLimit);

        String memberId1 = "memberId1";
        String memberId2 = "memberId2";
        String memberId3 = "memberId3";
        List<String> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        Coupon notIssuedCoupon = createCoupons(
                List.of(memberId3)
        ).get(0);

        // when
        List<Coupon> coupons = couponIssuer.issue(memberIds);

        // then
        assertThat(coupons).hasSize(2)
                .doesNotContain(notIssuedCoupon);
    }

    @Test
    void 쿠폰_수량을_전부_소진시키고_쿠폰을_발행하면_빈_리스트를_가져온다() {
        // given
        int couponLimit = 2;
        CouponIssuer couponIssuer = createCouponIssuer(couponLimit);

        String memberId1 = "memberId1";
        String memberId2 = "memberId2";
        String memberId3 = "memberId3";
        List<String> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        couponIssuer.issue(memberIds);

        // when
        List<Coupon> coupons = couponIssuer.issue(memberIds);

        // then
        assertThat(coupons).isEmpty();
    }

    @Test
    void 동시에_3개의_스레드가_쿠폰들을_발급_받을_수_있다() throws InterruptedException {
        // given
        int couponLimit = 100;
        CouponIssuer couponIssuer = createCouponIssuer(couponLimit);

        String memberId1 = "memberId1";
        List<String> memberIds = List.of(
                memberId1
        );

        int threadCount = couponLimit - 1;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    couponIssuer.issue(memberIds);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        List<Coupon> first = couponIssuer.issue(memberIds);
        List<Coupon> second = couponIssuer.issue(memberIds);

        assertThat(first).hasSize(1);
        assertThat(second).isEmpty();
    }

}