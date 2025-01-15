package my.event.practice.infra;

import my.event.practice.domain.Coupon;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SynchronizedCouponIssuerTest {

    private static SynchronizedCouponIssuer createCouponIssuer(int couponLimit) {
        return new SynchronizedCouponIssuer(couponLimit);
    }

    private static List<Coupon> createCoupons(List<Long> memberIds) {
        return memberIds.stream()
                .map(Coupon::new)
                .collect(Collectors.toList());
    }

    @Test
    void 쿠폰_발급기는_쿠폰을_발급할_수_있다() {
        // given
        int couponLimit = 3;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        List<Long> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        List<Coupon> expectedCoupons = createCoupons(memberIds);

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).containsAll(expectedCoupons);
    }

    @Test
    void 쿠폰_수량이_전부_소진되면_빈_리스트가_리턴된다() {
        // given
        int couponLimit = 0;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        List<Long> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).isEmpty();
    }

    @Test
    void 쿠폰_수량보다_회원_수가_많으면_쿠폰_수량_만큼만_쿠폰을_발행한다() {
        // given
        int couponLimit = 2;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        List<Long> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        Coupon notIssuedCoupon = createCoupons(
                List.of(memberId3)
        ).get(0);

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).hasSize(2)
                .doesNotContain(notIssuedCoupon);
    }

    @Test
    void 쿠폰_수량을_전부_소진시키고_쿠폰을_발행하면_빈_리스트를_가져온다() {
        // given
        int couponLimit = 2;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        List<Long> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        synchronizedCouponIssuer.issue(memberIds);

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).isEmpty();
    }

    @Test
    void 빈_회원_리스트로_쿠폰을_발행하면_빈_리스트를_가져온다() {
        // given
        int couponLimit = 2;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        List<Long> memberIds = List.of();

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).isEmpty();
    }

    @Test
    void 쿠폰_수량과_회원_수가_같으면_쿠폰_발급기는_닫힌다() {
        // given
        int couponLimit = 3;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        List<Long> memberIds = List.of(
                memberId1, memberId2, memberId3
        );

        synchronizedCouponIssuer.issue(memberIds);

        // when
        boolean isClose = synchronizedCouponIssuer.isClose();

        // then
        assertThat(isClose).isTrue();
    }

    @Test
    void 쿠폰_수량보다_회원_수가_많으면_쿠폰_수량_만큼만_쿠폰을_발행하고_쿠폰_발급기는_닫힌다() {
        // given
        int couponLimit = 1;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        Long memberId2 = 2L;
        List<Long> memberIds = List.of(
                memberId1, memberId2
        );

        // when
        List<Coupon> coupons = synchronizedCouponIssuer.issue(memberIds);

        // then
        assertThat(coupons).hasSize(1);
        assertThat(synchronizedCouponIssuer.isClose()).isTrue();
    }

    @Test
    void 동시에_3개의_스레드가_쿠폰들을_발급_받을_수_있다() throws InterruptedException {
        // given
        int couponLimit = 100;
        SynchronizedCouponIssuer synchronizedCouponIssuer = createCouponIssuer(couponLimit);

        Long memberId1 = 1L;
        List<Long> memberIds = List.of(
                memberId1
        );

        int threadCount = couponLimit - 1;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    synchronizedCouponIssuer.issue(memberIds);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        List<Coupon> first = synchronizedCouponIssuer.issue(memberIds);
        List<Coupon> second = synchronizedCouponIssuer.issue(memberIds);

        assertThat(first).hasSize(1);
        assertThat(second).isEmpty();
    }

}
