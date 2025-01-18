package my.event.practice.infra;

import my.event.practice.domain.Coupon;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class JdbcCouponRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static Time createTime() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 2, 1, 1);
        Time time = mock(Time.class);
        when(time.now()).thenReturn(now);
        return time;
    }

    // rewriteBatchedStatements를 true로 하지 않으면 insert가 개별로 나감 (profileSQL을 true로 하면 볼 수 있음)
    @Test
    void 쿠폰_저장소에_여러_개의_쿠폰을_한_번에_저장할_수_있다() {
        // given
        Time time = createTime();
        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        JdbcCouponRepository couponRepository = new JdbcCouponRepository(jdbcTemplate, time);

        // when
        couponRepository.saveAll(
                List.of(
                        new Coupon(memberId1),
                        new Coupon(memberId2),
                        new Coupon(memberId3)
                )
        );

        // then
        assertThat(couponRepository.findByMemberId(memberId1)).isPresent();
        assertThat(couponRepository.findByMemberId(memberId2)).isPresent();
        assertThat(couponRepository.findByMemberId(memberId3)).isPresent();
    }

    @Test
    void 쿠폰_저장소에_존재하지_않은_쿠폰을_조회하면_빈_옵셔널을_리턴한다() {
        // given
        Time time = createTime();
        Long memberId1 = 1L;
        JdbcCouponRepository couponRepository = new JdbcCouponRepository(jdbcTemplate, time);

        // when
        Optional<Coupon> coupon = couponRepository.findByMemberId(memberId1);

        // then
        assertThat(coupon).isEmpty();
    }

    @Test
    void 쿠폰_저장소에서_여러_개의_쿠폰을_한_번에_조회할_수_있다() {
        // given
        Time time = createTime();
        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
        JdbcCouponRepository couponRepository = new JdbcCouponRepository(jdbcTemplate, time);

        List<Coupon> coupons = List.of(
                new Coupon(memberId1),
                new Coupon(memberId2),
                new Coupon(memberId3)
        );
        couponRepository.saveAll(coupons);

        // when
        List<Coupon> savedCoupons = couponRepository.findAll();

        // then
        assertThat(savedCoupons)
                .hasSize(3)
                .containsAll(coupons);
    }

}
