package my.event.practice.support;

import my.event.practice.domain.Coupon;
import my.event.practice.domain.CouponIssuer;
import my.event.practice.domain.CouponRepository;
import my.event.practice.domain.DuplicateChecker;
import my.event.practice.infra.BitSetDuplicateCheckerManager;
import my.event.practice.infra.SynchronizedCouponIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.stream.Collectors;

// 서버 다운 시 복구 로직을 추가하기 위해, 수동 빈 등록 진행
@Configuration
public class EventPreparationConfig {

    private final CouponRepository couponRepository;
    private final long memberSize;
    private final int bitSetSize;
    private int couponLimit;

    public EventPreparationConfig(
            CouponRepository couponRepository,
            @Value("${member.size}") long memberSize,
            @Value("${bit-set.size:64}") int bitSetSize,
            @Value("${coupon.limit:100}") int couponLimit)
    {
        this.couponRepository = couponRepository;
        this.memberSize = memberSize;
        this.bitSetSize = bitSetSize;
        this.couponLimit = couponLimit;
    }

    @Bean
    public DuplicateChecker duplicateChecker() {
        List<Long> memberIds = couponRepository.findAll()
                .stream()
                .map(Coupon::getMemberId)
                .collect(Collectors.toList());
        couponLimit -= memberIds.size();
        return new BitSetDuplicateCheckerManager(memberSize, bitSetSize, memberIds);
    }

    @DependsOn("duplicateChecker")
    @Bean
    public CouponIssuer couponIssuer() {
        return new SynchronizedCouponIssuer(couponLimit);
    }

    // 테스트 코드에서만 사용
    int getCouponLimit() {
        return couponLimit;
    }
}
