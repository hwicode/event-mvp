package my.event.practice.support;

import my.event.practice.domain.Coupon;
import my.event.practice.domain.CouponRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventPreparationConfigTest {

    private static CouponRepository createCouponRepository(int numberOfCoupons) {
        List<Coupon> coupons = new ArrayList<>();
        for (long i = 1; i <= numberOfCoupons; i++) {
            coupons.add(new Coupon(i));
        }

        CouponRepository couponRepository = mock(CouponRepository.class);
        when(couponRepository.findAll())
                .thenReturn(coupons);
        return couponRepository;
    }

    @ValueSource(ints = {0, 5, 10, 30, 50})
    @ParameterizedTest
    void 회원_중복_검사기를_생성할_때_이미_처리된_회원을_추가한다(int numberOfCoupons) {
        // given
        int couponLimit = 100;
        CouponRepository couponRepository = createCouponRepository(numberOfCoupons);
        EventPreparationConfig eventPreparationConfig = new EventPreparationConfig(couponRepository, couponLimit, couponLimit, couponLimit);

        // when
        eventPreparationConfig.duplicateChecker();

        // then
        int realCouponLimit = eventPreparationConfig.getCouponLimit();
        assertThat(realCouponLimit).isEqualTo(couponLimit - numberOfCoupons);
    }

    @ValueSource(ints = {0, 5, 10, 30, 50})
    @ParameterizedTest
    void 쿠폰_발급기를_생성할_때_이미_처리된_쿠폰을_발급_처리한다(int numberOfCoupons) {
        // given
        int couponLimit = 100;
        CouponRepository couponRepository = createCouponRepository(numberOfCoupons);
        EventPreparationConfig eventPreparationConfig = new EventPreparationConfig(couponRepository, couponLimit, couponLimit, couponLimit);

        eventPreparationConfig.duplicateChecker();

        // when
        eventPreparationConfig.couponIssuer();

        // then
        int realCouponLimit = eventPreparationConfig.getCouponLimit();
        assertThat(realCouponLimit).isEqualTo(couponLimit - numberOfCoupons);
    }
}
